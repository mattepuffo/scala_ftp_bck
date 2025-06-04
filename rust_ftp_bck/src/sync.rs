use duckdb::Connection;
use prettytable::{color, Attr, Cell, Row, Table};
use std::path::{Path, PathBuf};

#[derive(Debug, Clone)]
pub(crate) struct MySync {
  pub(crate) key: String,
  pub(crate) value: String,
  pub(crate) server: String,
  pub(crate) path: String,
}

static DB_DIR: &'static str = "DATABASE";
static DB_FILE: &'static str = "db.duckdb";

pub fn get_all_sync() {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  let mut stmt = conn
      .prepare("SELECT key, value, server, path FROM sync ORDER BY key ASC")
      .unwrap();

  let rows = stmt
      .query_map([], |row| {
        let key: String = row.get(0)?;
        let value: String = row.get(1)?;
        let server: String = row.get(2)?;
        let path = row.get(3)?;

        Ok(MySync { key, value, server, path })
      })
      .unwrap();

  let mut table = Table::new();
  table.add_row(Row::new(vec![
    Cell::new("NOME")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::BLUE)),
    Cell::new("CARTELLA")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::CYAN)),
    Cell::new("SERVER")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::MAGENTA)),
    Cell::new("PATH")
        .with_style(Attr::Bold)
        .with_style(Attr::ForegroundColor(color::GREEN)),
  ]));

  for row in rows {
    let sync = row.unwrap();

    table.add_row(Row::new(vec![
      Cell::new(&sync.key),
      Cell::new(&sync.value),
      Cell::new(&sync.server),
      Cell::new(&sync.path),
    ]));
  }

  table.printstd();
  println!("=====");
}

pub fn get_sync_by_key(k: &str) -> Result<MySync, String> {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path)
      .map_err(|e| e.to_string())?;

  let mut stmt = conn
      .prepare("SELECT key, value, server, path FROM sync WHERE key = ?")
      .map_err(|e| e.to_string())?;

  let mut rows = stmt.query([k])
      .map_err(|e| e.to_string())?;

  if let Some(row) = rows.next().map_err(|e| e.to_string())? {
    Ok(MySync {
      key: row.get(0).map_err(|e| e.to_string())?,
      value: row.get(1).map_err(|e| e.to_string())?,
      server: row.get(2).map_err(|e| e.to_string())?,
      path: row.get(3).map_err(|e| e.to_string())?,
    })
  } else {
    Err("Non è stato trovato alcun record".to_string())
  }
}

pub fn add_sync(key: &str, value: &str, server: &str, path: &str) {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  conn.execute(
    "INSERT INTO sync (key, value, server, path) VALUES (?, ?, ?, ?) ON CONFLICT DO UPDATE SET value = ?, server = ?, path = ?",
    [
      // INSERT
      key,
      value,
      server,
      path,
      // UPDATE
      value,
      server,
      path,
    ],
  )
      .expect("ERRORE DI INSERIMENTO NELLA TABELLA sync");

  println!("OPERAZIONE AVVENUTA CON SUCCESSO!");
  println!("=====");
}

pub fn del_sync(key: &str) {
  let db_path: PathBuf = Path::new(DB_DIR).join(DB_FILE);
  let conn = Connection::open(db_path).unwrap();

  conn.execute(
    "DELETE FROM sync WHERE key = ?",
    [key],
  )
      .expect("ERRORE DI CANCELLAZIONE NELLA TABELLA ftp");

  println!("OPERAZIONE AVVENUTA CON SUCCESSO!");
  println!("=====");
}