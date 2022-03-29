import { MaterialIcons } from "@expo/vector-icons";
import { Column, ScrollView, Spinner } from "native-base";
import React from "react";

///////////////////////////////////////////////////////////////////////////////
import colors from "../util/colors";
import config from "../util/config";
import PlatformListItem from "./PlatformListItem";
import util from "../util/util";

///////////////////////////////////////////////////////////////////////////////
const PlatformListScreen = ({ navigation, route }) => {
  const { model, path } = route.params;
  const [platformList, setPlatformList] = React.useState([]);

  React.useEffect(() => {
    return navigation.addListener("focus", async () => {
      setPlatformList([]);
      const { host, port } = config;
      const url = `http://${host}:${port}/scan/rbf/${path}`;
      try {
        const response = await fetch(url);
        const entries = await response.json();
        const arr = [];
        Object.keys(model).forEach((key) => {
          const platform = model[key];
          const match = entries.find((path) => {
            const filename = util.getFileName({ path });
            return filename.startsWith(platform.core);
          });
          if (match) {
            arr.push(platform);
          }
        });
        setPlatformList(arr);
      } catch (error) {
        setPlatformList(null);
      }
    });
  }, [navigation]);

  if (platformList === null) {
    return (
      <Column alignItems="center" justifyContent="center" style={{ flex: 1 }}>
        <MaterialIcons color={colors.blue} name="error-outline" size={40} />
      </Column>
    );
  } else if (platformList.length > 0) {
    return (
      <ScrollView bg="black">
        {platformList.map((platform, index) => {
          return (
            <PlatformListItem
              key={index}
              navigation={navigation}
              platform={platform}
            />
          );
        })}
      </ScrollView>
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
export default PlatformListScreen;
