import "react-native-gesture-handler"; // Must go first

///////////////////////////////////////////////////////////////////////////////
import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { NativeBaseProvider, StatusBar, View } from "native-base";
import { SafeAreaProvider } from "react-native-safe-area-context";
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
        <View style={{ flex: 1, backgroundColor: "#000" }}>
          <NavigationContainer theme={DarkTheme}>
            <NativeStackNavigator />
          </NavigationContainer>
        </View>
      </NativeBaseProvider>
    </SafeAreaProvider>
  );
};

///////////////////////////////////////////////////////////////////////////////
export default App;
