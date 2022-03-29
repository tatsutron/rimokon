import { MaterialIcons } from "@expo/vector-icons";
import { Column, Spinner } from "native-base";
import React from "react";
import { Dimensions } from "react-native";
import {
  RecyclerListView,
  DataProvider,
  LayoutProvider,
} from "recyclerlistview";

///////////////////////////////////////////////////////////////////////////////
import colors from "../util/colors";
import config from "../util/config";
import dimensions from "../util/dimensions";
import GameListItem from "./GameListItem";

///////////////////////////////////////////////////////////////////////////////
const rowRenderer = (_type, data, _index) => {
  const { navigation, path, platform } = data;
  return (
    <GameListItem navigation={navigation} path={path} platform={platform} />
  );
};

///////////////////////////////////////////////////////////////////////////////
const GameListScreen = ({ navigation, route }) => {
  const { path, platform } = route.params;
  const [dataProvider, setDataProvider] = React.useState(
    new DataProvider((r1, r2) => {
      return r1 !== r2;
    })
  );
  const [layoutProvider] = React.useState(
    new LayoutProvider(
      (index) => 1,
      (type, dim) => {
        dim.width = Dimensions.get("window").width;
        dim.height = dimensions.rowHeight;
      }
    )
  );
  const [error, setError] = React.useState(false);

  React.useEffect(() => {
    return navigation.addListener("focus", async () => {
      setError(false);
      const { host, port } = config;
      const extensions = platform.format
        .map((format) => {
          return format.extension;
        })
        .reduce((previousValue, currentValue) => {
          return `${previousValue}|${currentValue}`;
        });
      const url = `http://${host}:${port}/scan/${extensions}/${path}`;
      try {
        const response = await fetch(url);
        const entries = await response.json();
        const gameList = entries
          .sort((a, b) => {
            return a.toLowerCase().localeCompare(b.toLowerCase());
          })
          .map((path) => {
            return { navigation, path, platform };
          });
        setDataProvider((prevState) => prevState.cloneWithRows(gameList));
      } catch (error) {
        setError(true);
      }
    });
  }, [navigation]);

  if (error) {
    return (
      <Column alignItems="center" justifyContent="center" style={{ flex: 1 }}>
        <MaterialIcons color={colors.blue} name="error-outline" size={40} />
      </Column>
    );
  } else if (dataProvider.getSize() > 0) {
    return (
      <RecyclerListView
        dataProvider={dataProvider}
        layoutProvider={layoutProvider}
        rowRenderer={rowRenderer}
        style={{ flex: 1 }}
      />
    );
  } else {
    return (
      <Column alignItems="center" justifyContent="center" style={{ flex: 1 }}>
        <Spinner size="lg" color={colors.blue} />
      </Column>
    );
  }
};

//////////////////////////////////////////////////////////////////////////////
export default GameListScreen;
