import React from "react";

///////////////////////////////////////////////////////////////////////////////
import { Column, FormControl, Input } from "native-base";

///////////////////////////////////////////////////////////////////////////////
import config from "../util/config";
import colors from "../util/colors";

///////////////////////////////////////////////////////////////////////////////
const ConfigScreen = () => {
  return (
    <Column alignItems="center" justifyContent="center" style={{ flex: 1 }}>
      <FormControl w="75%" maxW="300px">
        <FormControl.Label _text={{ color: "white" }}>
          Hostname or IP Address
        </FormControl.Label>
        <Input
          _focus={{ borderColor: colors.bue }}
          borderColor="white"
          color={colors.blue}
          onChangeText={(text) => (config.host = text)}
          placeholder={config.host}
          placeholderTextColor={colors.blue}
          selectionColor={colors.blue}
        />
      </FormControl>
    </Column>
  );
};

//////////////////////////////////////////////////////////////////////////////
export default ConfigScreen;
