import { Asset } from "expo-asset";
import * as FileSystem from "expo-file-system";
import * as SQLite from "expo-sqlite";

///////////////////////////////////////////////////////////////////////////////
import config from "./config";

///////////////////////////////////////////////////////////////////////////////
let db = null;

///////////////////////////////////////////////////////////////////////////////
const getExtension = ({ path }) => /(?:\.([^.]+))?$/.exec(path)[1];

///////////////////////////////////////////////////////////////////////////////
const getFileName = ({ path }) =>
  path
    .split("/")
    .pop()
    .replace(`.${getExtension({ path })}`, "");

///////////////////////////////////////////////////////////////////////////////
const getFolderName = ({ path }) =>
  path
    .split("/")
    .map((token) => {
      return token.match(/[^ ]+/g);
    })
    .filter((token) => {
      return token != null;
    })
    .pop();

///////////////////////////////////////////////////////////////////////////////
const getMetadata = ({ hash }) => {
  const sql = [
    "SELECT",
    [
      "r2.releaseCoverBack AS backCover",
      "r2.releaseCoverCart AS cartridge",
      "r2.releaseDescription AS description",
      "r2.releaseDeveloper AS developer",
      "r2.releaseCoverFront AS frontCover",
      "r2.releaseGenre AS genre",
      "r2.releasePublisher AS publisher",
      "r2.releaseDate AS releaseDate",
      "r1.regionName AS region",
    ].join(", "),
    "FROM REGIONS r1 JOIN RELEASES r2",
    `ON r1.regionId = (${[
      "SELECT regionID FROM ROMs",
      `WHERE romHashSHA1 = '${hash.toUpperCase()}'`,
    ].join(" ")})`,
    `AND r2.romID = (${[
      "SELECT romID from ROMs",
      `WHERE romHashSHA1 = '${hash.toUpperCase()}'`,
    ].join(" ")})`,
    "LIMIT 1",
  ].join(" ");

  return new Promise((resolve) => {
    const success = () => {};
    const error = (error) => {
      resolve(error.message);
    };
    db.transaction(
      (tx) => {
        tx.executeSql(sql, [], (_, { rows }) => {
          resolve(rows._array[0]);
        });
      },
      error,
      success
    );
  });
};

///////////////////////////////////////////////////////////////////////////////
const init = async () => {
  if (db !== null) {
    return;
  }
  const dir = FileSystem.documentDirectory + "SQLite";
  if (!(await FileSystem.getInfoAsync(dir)).exists) {
    await FileSystem.makeDirectoryAsync(dir);
  }
  await FileSystem.downloadAsync(
    Asset.fromModule(require("../assets/openvgdb.sqlite")).uri,
    dir + "/openvgdb.sqlite"
  );
  db = SQLite.openDatabase("openvgdb.sqlite");
};

///////////////////////////////////////////////////////////////////////////////
const isFolder = ({ path }) => path.endsWith("/");

///////////////////////////////////////////////////////////////////////////////
const loadGame = ({ platform, path }) => {
  const { host, port, volume } = config;
  if (platform.mgl) {
    const games = `${volume}games/${platform.folder}/`;
    const url = `http://${host}:${port}/play`;
    const init = {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        path: path.replace(games, ""),
        ...platform.mgl,
      }),
    };
    try {
      fetch(url, init);
    } catch (error) {
      alert(error);
    }
  } else {
    const command = platform.format.find((format) => {
      return format.extension === getExtension({ path });
    }).mbcCommand;
    const url = `http://${host}:${port}/load/${command}/${path}`;
    try {
      fetch(url);
    } catch (error) {
      alert(error);
    }
  }
};

///////////////////////////////////////////////////////////////////////////////
export default {
  getExtension,
  getFileName,
  getFolderName,
  getMetadata,
  init,
  isFolder,
  loadGame,
};
