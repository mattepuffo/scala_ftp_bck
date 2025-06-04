use std::{fmt};
use std::path::{Path, PathBuf};
use curl::easy::Easy;
use duckdb::Connection;
use prettytable::{color, Attr, Cell, Row, Table};
use std::fs::File;
use std::io::Read;

#[derive(Debug, Clone)]
pub(crate) struct FtpServer {
  pub(crate) name: String,
  pub(crate) host: String,
  pub(crate) username: String,
  pub(crate) password: String,
}

impl fmt::Display for FtpServer {
  fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
    write!(f, "{}", self.name)
  }
}

static DB_DIR: &'static str = "DATABASE";
static DB_FILE: &'static str = "db.duckdb";

pub fn get_all_server() {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  let mut stmt = conn
      .prepare("SELECT name, host, username, password FROM ftp ORDER BY name DESC")
      .unwrap();

  let rows = stmt
      .query_map([], |row| {
        let name: String = row.get(0)?;
        let host: String = row.get(1)?;
        let username: String = row.get(2)?;
        let password: String = row.get(3)?;

        Ok(FtpServer {
          name,
          host,
          username,
          password,
        })
      })
      .unwrap();

  let mut table = Table::new();
  table.add_row(Row::new(vec![
    Cell::new("NOME")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::BLUE)),
    Cell::new("HOST")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::CYAN)),
    Cell::new("USERNAME")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::MAGENTA)),
    Cell::new("PASSWORD")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::GREEN)),
  ]));

  for row in rows {
    let item = row.unwrap();

    table.add_row(Row::new(vec![
      Cell::new(&item.name),
      Cell::new(&item.host),
      Cell::new(&item.username),
      Cell::new(&item.password),
    ]));
  }

  table.printstd();
  println!("=====");
}

pub fn get_server_by_name(k: &str) -> Result<FtpServer, String> {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path)
      .map_err(|e| e.to_string())?;

  let mut stmt = conn
      .prepare("SELECT name, host, username, password FROM ftp WHERE name = ?")
      .map_err(|e| e.to_string())?;

  let mut rows = stmt.query([k])
      .map_err(|e| e.to_string())?;

  if let Some(row) = rows.next().map_err(|e| e.to_string())? {
    Ok(FtpServer {
      name: row.get(0).map_err(|e| e.to_string())?,
      host: row.get(1).map_err(|e| e.to_string())?,
      username: row.get(2).map_err(|e| e.to_string())?,
      password: row.get(3).map_err(|e| e.to_string())?,
    })
  } else {
    Err("Non è stato trovato alcun record".to_string())
  }
}

pub fn add_server(name: &str, host: &str, username: &str, password: &str) {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  conn.execute(
    "INSERT INTO ftp (name, host, username, password) VALUES (?, ?, ?, ?) ON CONFLICT DO UPDATE SET username = ?, password = ?",
    [name, host, username, password, username, password],
  )
      .expect("ERRORE DI INSERIMENTO NELLA TABELLA ftp");

  println!("OPERAZIONE AVVENUTA CON SUCCESSO!");
  println!("=====");
}

pub fn del_server(name: &str) {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  conn.execute(
    "DELETE FROM ftp WHERE name = ?",
    [name],
  )
      .expect("ERRORE DI CANCELLAZIONE NELLA TABELLA ftp");

  println!("OPERAZIONE AVVENUTA CON SUCCESSO!");
  println!("=====");
}

pub fn do_upload(server: &str, username: &str, password: &str, dir: &str, file_upload: &str, remote_name: &str) -> Result<(), Box<dyn std::error::Error>> {
  let mut file = File::open(file_upload)?;
  let mut buffer = Vec::new();
  file.read_to_end(&mut buffer)?;

  let mut easy = Easy::new();
  let ftp_url = format!("ftp://{}:{}/{}{}", server, 21, dir, remote_name);
  easy.url(&ftp_url)?;
  easy.username(username)?;
  easy.password(password)?;
  easy.upload(true)?;

  let mut transfer = easy.transfer();
  transfer.read_function(|buf| {
    Ok(buffer.as_slice().read(buf).unwrap_or(0))
  })?;

  transfer.perform()?;

  Ok(())
}