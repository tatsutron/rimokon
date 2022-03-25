import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { Column, FormControl, Input } from "native-base";

///////////////////////////////////////////////////////////////////////////////
import config from "../util/config";

///////////////////////////////////////////////////////////////////////////////
const ConfigScreen = () => {
  return (
    <Column alignItems="center" justifyContent="center" style={{ flex: 1 }}>
      <FormControl w="75%" maxW="300px">
        <FormControl.Label _text={{ color: "white" }}>
          Hostname or IP Address
        </FormControl.Label>
        <Input
          _focus={{ borderColor: "#28a4ea" }}
          borderColor="white"
          color="#28a4ea"
          onChangeText={(text) => (config.host = text)}
          placeholder={config.host}
          placeholderTextColor="#28a4ea"
          selectionColor="#28a4ea"
        />
      </FormControl>
    </Column>
  );
};

//////////////////////////////////////////////////////////////////////////////
export default ConfigScreen;
