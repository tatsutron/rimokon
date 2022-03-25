import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { AntDesign, Entypo } from "@expo/vector-icons";
import { Column, Pressable, Row, Text } from "native-base";

///////////////////////////////////////////////////////////////////////////////
import dimensions from "../util/dimensions";
import util from "../util/util";

///////////////////////////////////////////////////////////////////////////////
const GameListItem = (props) => {
  const { navigation, path, platform } = props;
  return (
    <Column
      justifyContent="center"
      style={{
        height: dimensions.rowHeight,
      }}
    >
      <Pressable
        onPress={() => {
          if (util.isFolder({ path })) {
            navigation.push("GameList", { path, platform });
          } else {
            navigation.navigate("GameDetail", { path, platform });
          }
        }}
      >
        <Row alignItems="center">
          {util.isFolder({ path }) ? (
            <AntDesign
              color="white"
              name="folder1"
              size={24}
              style={{
                marginLeft: 24,
              }}
            />
          ) : (
            <Entypo
              color="#28a4ea"
              name="dot-single"
              size={24}
              style={{
                marginLeft: 24,
              }}
            />
          )}
          <Text color="white" fontSize="lg" marginLeft={4}>
            {util.isFolder({ path })
              ? util.getFolderName({ path: props.path })
              : util.getFileName({ path: props.path })}
          </Text>
        </Row>
      </Pressable>
    </Column>
  );
};

//////////////////////////////////////////////////////////////////////////////
export default GameListItem;
