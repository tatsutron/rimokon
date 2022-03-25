import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { Ionicons, MaterialCommunityIcons } from "@expo/vector-icons";
import { Image } from "native-base";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";

///////////////////////////////////////////////////////////////////////////////
import arcades from "../model/arcades";
import computers from "../model/computers";
import config from "../util/config";
import ConfigScreen from "../component/ConfigScreen";
import consoles from "../model/consoles";
import GameListScreen from "../component/GameListScreen";
import handhelds from "../model/handhelds";
import PlatformListScreen from "../component/PlatformListScreen";

///////////////////////////////////////////////////////////////////////////////
const Tab = createBottomTabNavigator();

///////////////////////////////////////////////////////////////////////////////
const BottomTabNavigator = () => {
  return (
    <Tab.Navigator
      screenOptions={{
        headerTintColor: "white",
        tabBarActiveTintColor: "#28a4ea",
        tabBarStyle: {
          backgroundColor: "black",
        },
      }}
    >
      <Tab.Screen
        component={PlatformListScreen}
        initialParams={{
          model: consoles,
          path: config.console,
        }}
        name="ConsoleList"
        options={{
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons
              color={color}
              name="gamepad-square"
              size={size}
            />
          ),
          title: "Consoles",
        }}
      />
      <Tab.Screen
        component={PlatformListScreen}
        initialParams={{
          model: handhelds,
          path: config.console,
        }}
        name="HandheldList"
        options={{
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons
              color={color}
              name="nintendo-game-boy"
              size={size}
            />
          ),
          title: "Handhelds",
        }}
      />
      <Tab.Screen
        component={GameListScreen}
        initialParams={{
          path: config.arcade,
          platform: arcades[0],
        }}
        name="ArcadeList"
        options={{
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <Image
              alt="Icon"
              fadeDuration={0}
              source={require("../assets/joystick-icon.png")}
              style={{ width: size, height: size }}
              tintColor={color}
            />
          ),
          title: "Arcades",
        }}
      />
      <Tab.Screen
        component={PlatformListScreen}
        initialParams={{
          model: computers,
          path: config.computer,
        }}
        name="ComputerList"
        options={{
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons color={color} name="keyboard" size={size} />
          ),
          title: "Computers",
        }}
      />
      <Tab.Screen
        component={ConfigScreen}
        initialParams={{
          model: computers,
          path: config.computer,
        }}
        name="Config"
        options={{
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <Ionicons color={color} name="settings-outline" size={size} />
          ),
          title: "Config",
        }}
      />
    </Tab.Navigator>
  );
};

///////////////////////////////////////////////////////////////////////////////
export default BottomTabNavigator;
