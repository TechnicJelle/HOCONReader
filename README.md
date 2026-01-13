# HOCON Reader

A tiny Java program to read values from [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) configuration files through JSON.

## Example usages

HOCON to JSON:

```bash
$ java -jar HOCONReader.jar /home/technicjelle/Documents/BlueMapGUI/1_13_2/config/maps/overworld.conf
{
  "world": "/home/technicjelle/.minecraft/saves/1_13_2/",
  "dimension": "minecraft:overworld",
  "name": "Overworld",
  "sorting": 0,
  "sky-color": "#7dabff",
  "void-color": "#000000",
  "sky-light": 1,
  "ambient-light": 0.1,
  "remove-caves-below-y": 55,
  "cave-detection-ocean-floor": -5,
  "cave-detection-uses-block-light": false,
  "min-inhabited-time": 0,
  "render-edges": true,
  "enable-perspective-view": true,
  "enable-flat-view": true,
  "enable-free-flight-view": true,
  "enable-hires": true,
  "storage": "file",
  "ignore-missing-light-data": false,
  "marker-sets": { }
}
```

Extract values from the resulting JSON with [JSONPath](https://github.com/json-path/JsonPath):

```bash
$ java -jar HOCONReader.jar /home/technicjelle/Documents/BlueMapGUI/1_13_2/config/maps/overworld.conf $.world
/home/technicjelle/.minecraft/saves/1_13_2/
```
