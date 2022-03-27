import "react-native-gesture-handler"; // Must go first

///////////////////////////////////////////////////////////////////////////////
import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { NativeBaseProvider, StatusBar, View } from "native-base";
import { SafeAreaProvider, SafeAreaView } from "react-native-safe-area-context";
import { DarkTheme, NavigationContainer } from "@react-navigation/native";

///////////////////////////////////////////////////////////////////////////////
import NativeStackNavigator from "./navigator/NativeStackNavigator";
import util from "./util/util";

///////////////////////////////////////////////////////////////////////////////
const App = () => {
  React.useEffect(async () => {
    await util.init();
  }, []);

  return (
    <SafeAreaProvider>
      <NativeBaseProvider>
        <SafeAreaView style={{ flex: 1, backgroundColor: "#000" }}>
          <StatusBar style="light" />
          <NavigationContainer theme={DarkTheme}>
            <NativeStackNavigator />
          </NavigationContainer>
        </SafeAreaView>
      </NativeBaseProvider>
    </SafeAreaProvider>
  );
};

///////////////////////////////////////////////////////////////////////////////
export default App;
