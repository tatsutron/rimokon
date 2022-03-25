import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { FlatList } from "native-base";

///////////////////////////////////////////////////////////////////////////////
import config from "../util/config";
import dimensions from "../util/dimensions";
import GameListItem from "./GameListItem";

///////////////////////////////////////////////////////////////////////////////
const getItemLayout = (_, index) => ({
  length: dimensions.rowHeight,
  offset: dimensions.rowHeight * index,
  index,
});

///////////////////////////////////////////////////////////////////////////////
const renderItem = ({ item }) => {
  return (
    <GameListItem
      key={item.key}
      navigation={item.navigation}
      path={item.path}
      platform={item.platform}
    />
  );
};

///////////////////////////////////////////////////////////////////////////////
const GameListScreen = ({ navigation, route }) => {
  const { path, platform } = route.params;
  const [gameList, setGameList] = React.useState([]);

  console.log("GameListScreen");

  React.useEffect(() => {
    return navigation.addListener("focus", async () => {
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
        setGameList(
          entries.sort((a, b) => {
            return a.toLowerCase().localeCompare(b.toLowerCase());
          })
        );
      } catch (error) {
        alert(error);
      }
    });
  }, [navigation]);

  return (
    <FlatList
      data={gameList.map((path) => {
        return {
          key: path,
          navigation,
          path,
          platform,
        };
      })}
      getItemLayout={getItemLayout}
      initialNumToRender={12}
      maxToRenderPerBatch={8}
      renderItem={renderItem}
    />
  );
};

//////////////////////////////////////////////////////////////////////////////
export default GameListScreen;
