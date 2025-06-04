mod db;
mod ftp;
mod log;
mod sync;
mod compress;

use dialoguer::theme::ColorfulTheme;
use dialoguer::{Input};
use std::io::{self, Write};
use std::process;
use colored::Colorize;
use crate::ftp::FtpServer;
use crate::sync::MySync;

#[tokio::main]
pub async fn main() -> Result<(), Box<dyn std::error::Error>> {
  ftp::do_upload("ftp.mattepuffo.com",
                         "1814342@aruba.it",
                         "Fermat852021",
                         "/www.mattepuffo.com/BCK",
                         "C:\\PROJECT\\rust_ftp_bck\\BCK\\Documenti.zip",
                         "Documenti.zip",
  )?;

  Ok(())
}

// fn main() {
//   test();
//
//   db::create_db();
//
//   let opzioni = vec!["FTP", "SYNC", "LOG", "BACKUP DB", "PULISCI BCK", "ESCI"];
//
//   loop {
//     for (i, opzione) in opzioni.iter().enumerate() {
//       println!("{}: {}", i + 1, opzione);
//     }
//
//     let scelta: usize = Input::with_theme(&ColorfulTheme::default())
//         .with_prompt("SELEZIONA UN'OPZIONE")
//         .interact_text()
//         .expect("ERRORE NELLA LETTURA DELL'INPUT");
//
//     // let scelta = Select::with_theme(&ColorfulTheme::default())
//     //     .with_prompt("Menu")
//     //     .default(0)
//     //     .items(&opzioni)
//     //     .interact()
//     //     .expect("ERRORE NELLA LETTURA DELL'INPUT");
//
//     match scelta {
//       1 => gestione_ftp(),
//       2 => gestione_sync(),
//       3 => gestione_log(),
//       4 => db::copy_db(),
//       5 => compress::delete_file(),
//       6 => {
//         println!("{}", "USCITA...".yellow());
//         break;
//       }
//       _ => {
//         println!("{}", "NESSUNA VOCE CORRISPONDENTE...".red());
//         break;
//       }
//     }
//   }
// }

fn gestione_sync() {
  let opzioni = vec!["VISUALIZZA SYNC", "AGGIUNGI SYNC", "CANCELLA SYNC", "INDIETRO"];

  loop {
    for (i, opzione) in opzioni.iter().enumerate() {
      println!("{}: {}", i + 1, opzione);
    }

    let scelta: usize = Input::with_theme(&ColorfulTheme::default())
        .with_prompt("GESTIONE SYNC")
        .interact_text()
        .expect("ERRORE NELLA LETTURA DELL'INPUT");

    match scelta {
      1 => sync::get_all_sync(),
      2 => {
        println!("{}", "SCRIVI DUE VALORI DEL SYNC".blue());
        println!("{}", "NOME, PATH DA COMPRIMERE E SERVER SEPARATI DAL CARATTERE |".blue());
        println!("{}", "NOTA: CONVIENE PRIMA CREARE IL SERVER E ANNOTARSI IL NOME".yellow());
        println!("{}", "AD ESEMPIO: nome1 | /home/fermat | server1 | cartella".yellow());

        io::stdout().flush().unwrap();

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();

        let mut parts = input.trim().split("|");

        let k = parts.next().unwrap_or("").trim();
        let v = parts.next().unwrap_or("").trim();
        let s = parts.next().unwrap_or("").trim();
        let p = parts.next().unwrap_or("").trim();

        sync::add_sync(&*k, &*v, &*s, &*p);
      }
      3 => {
        println!("{}", "CANCELLA UN SYNC".blue());
        println!("{}", "SCRIVI IL NOME DEL SYNC CHE VUOI CANCELLARE".blue());
        println!("{}", "AD ESEMPIO: nome1".yellow());

        io::stdout().flush().unwrap();

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();

        let name = input.trim();

        sync::del_sync(&*name);
        sync::get_all_sync();
      }
      4 => break,
      _ => {
        println!("{}", "NESSUNA VOCE CORRISPONDENTE...".red());
        break;
      }
    }
  }
}

fn gestione_log() {
  let opzioni = vec!["LEGGI LOG", "CANCELLA LOG", "INDIETRO"];

  loop {
    for (i, opzione) in opzioni.iter().enumerate() {
      println!("{}: {}", i + 1, opzione);
    }

    let scelta: usize = Input::with_theme(&ColorfulTheme::default())
        .with_prompt("GESTIONE LOG")
        .interact_text()
        .expect("ERRORE NELLA LETTURA DELL'INPUT");

    match scelta {
      1 => log::read_log(),
      2 => log::clear_log(),
      3 => break,
      _ => {
        println!("{}", "NESSUNA VOCE CORRISPONDENTE...".red());
        break;
      }
    }
  }
}

fn gestione_ftp() {
  let opzioni = vec!["ESEGUI BCK", "VISUALIZZA SERVER", "AGGIUNGI SERVER", "CANCELLA SERVER", "INDIETRO"];

  loop {
    for (i, opzione) in opzioni.iter().enumerate() {
      println!("{}: {}", i + 1, opzione);
    }

    let scelta: usize = Input::with_theme(&ColorfulTheme::default())
        .with_prompt("GESTIONE FTP")
        .interact_text()
        .expect("ERRORE NELLA LETTURA DELL'INPUT");

    match scelta {
      1 => {
        println!("{}", "SCRIVI SYNC E FTP SERVER SEPARATI DAL CARATTERE |".blue());
        println!("{}", "AD ESEMPIO: nome1 | server1".yellow());

        io::stdout().flush().unwrap();

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();

        let mut parts = input.trim().split("|");

        let sync_name = parts.next().unwrap_or("").trim();
        let ftp_server = parts.next().unwrap_or("").trim();

        let directory_to_zip: MySync = match sync::get_sync_by_key(sync_name) {
          Ok(sync) => sync,
          Err(err) => {
            println!("Errore: {}", err.red());
            break;
          }
        };

        let upload_server: FtpServer = match ftp::get_server_by_name(ftp_server) {
          Ok(server) => server,
          Err(err) => {
            println!("Errore: {}", err.red());
            break;
          }
        };

        let mut _file_zipped = String::new();
        let res_zip = compress::compress_directory(&directory_to_zip.value);

        match res_zip {
          Ok(path) => _file_zipped = path,
          Err(e) => println!("Errore: {}", e.red()),
        }

        ftp::do_upload(
          &*upload_server.host,
          &*upload_server.username,
          &*upload_server.password,
          &*directory_to_zip.path,
          &*directory_to_zip.value,
          "doc.zip",
        );

        log::create_log("documenti");
      }
      2 => {
        ftp::get_all_server()
      }
      3 => {
        println!("{}", "CREA UN SERVER FTP".blue());
        println!("{}", "SCRIVI NOME, HOST, USERNAME, PASSWORD SEPARATI DAL CARATTERE |".blue());
        println!("{}", "AD ESEMPIO: nome1 | 127.0.0.1 | user1 | sdkjfdkjs".yellow());

        io::stdout().flush().unwrap();

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();

        let mut parts = input.trim().split("|");

        let name = parts.next().unwrap_or("").trim();
        let host = parts.next().unwrap_or("").trim();
        let username = parts.next().unwrap_or("").trim();
        let password = parts.next().unwrap_or("").trim();

        ftp::add_server(&*name, &*host, &*username, &*password);
      }
      4 => {
        println!("{}", "CANCELLA UN SERVER FTP".blue());
        println!("{}", "SCRIVI IL NOME DEL SERVER CHE VUOI CANCELLARE".blue());
        println!("{}", "AD ESEMPIO: nome1".yellow());

        io::stdout().flush().unwrap();

        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();

        let name = input.trim();

        ftp::del_server(&*name);
        ftp::get_all_server();
      }
      5 => break,
      _ => {
        println!("{}", "NESSUNA VOCE CORRISPONDENTE...".red());
        break;
      }
    }
  }
}

fn test() -> Result<(), Box<dyn std::error::Error>> {
  // let sync_name = "documenti";
  // let ftp_server = "aruba_mp";
  //
  // let directory_to_zip: MySync = match sync::get_sync_by_key(sync_name) {
  //   Ok(sync) => sync,
  //   Err(err) => {
  //     println!("Errore: {}", err.red());
  //     return;
  //   }
  // };
  //
  // let upload_server: FtpServer = match ftp::get_server_by_name(ftp_server) {
  //   Ok(server) => server,
  //   Err(err) => {
  //     println!("Errore: {}", err.red());
  //     return;
  //   }
  // };
  //
  // let mut _file_zipped = String::new();
  // let res_zip = compress::compress_directory(&directory_to_zip.value);
  //
  // match res_zip {
  //   Ok(path) => _file_zipped = path,
  //   Err(e) => println!("Errore: {}", e.red()),
  // }

  // ftp::do_upload(
  //   &*upload_server.host,
  //   &*upload_server.username,
  //   &*upload_server.password,
  //   &*directory_to_zip.path,
  //   &*directory_to_zip.value,
  //   "documenti.zip",
  // );

  let _ = ftp::do_upload("ftp.mattepuffo.com",
                         "1814342@aruba.it",
                         "Fermat852021",
                         "/www.mattepuffo.com/BCK",
                         "C:\\PROJECT\\rust_ftp_bck\\BCK\\Documenti.zip",
                         "Documenti.zip",
  );

  process::exit(0);
}