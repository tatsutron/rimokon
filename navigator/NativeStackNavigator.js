import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { Button } from "native-base";
import { Platform } from "react-native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";

///////////////////////////////////////////////////////////////////////////////
import BottomTabNavigator from "./BottomTabNavigator";
import colors from "../util/colors";
import GameDetailScreen from "../component/GameDetailScreen";
import GameListScreen from "../component/GameListScreen";
import util from "../util/util";

///////////////////////////////////////////////////////////////////////////////
const NativeStack = createNativeStackNavigator();

///////////////////////////////////////////////////////////////////////////////
const NativeStackNavigator = () => {
  return (
    <NativeStack.Navigator>
      <NativeStack.Screen
        name="BottomTab"
        component={BottomTabNavigator}
        options={{
          headerShown: false,
        }}
      />
      <NativeStack.Screen
        name="GameList"
        component={GameListScreen}
        options={({ route }) => {
          const { platform } = route.params;
          return {
            title: platform.name,
          };
        }}
      />
      <NativeStack.Screen
        name="GameDetail"
        component={GameDetailScreen}
        options={({ route }) => {
          const { platform, path } = route.params;
          return {
            title: util.getFileName({ path }),
            headerRight: () => (
              <Button
                _pressed={{
                  backgroundColor: "black",
                  borderColor: "white",
                }}
                borderColor={colors.blue}
                marginRight={Platform.OS === "web" ? 3 : 0}
                onPress={() => {
                  util.loadGame({ platform, path });
                }}
                variant="outline"
              >
                PLAY
              </Button>
            ),
          };
        }}
      />
    </NativeStack.Navigator>
  );
};

///////////////////////////////////////////////////////////////////////////////
export default NativeStackNavigator;
