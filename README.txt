WEATHER CONTROL - Forge 1.20.1 (47.2.0)

COMO COMPILAR
1) Instala JDK 17.
2) Baja el MDK oficial de Forge 1.20.1 (1.20.1-47.2.0) desde files.minecraftforge.net
   y copia dentro de el la carpeta "src" de este proyecto (reemplazando la que trae)
   y el build.gradle de este proyecto (o solo ajusta mod id/nombre en el del MDK).
3) En la carpeta del proyecto: ./gradlew build   (Windows: gradlew.bat build)
4) El .jar queda en build/libs/ -> ponelo en la carpeta mods de Minecraft 1.20.1 con Forge.

COMO SE USA
- Creativo: pestaña "Control del Clima" (o /give @s weathercontrol:rain_item)
- Click derecho = activa. Click derecho otra vez = desactiva.
- Items: rain_item, snow_item, thunder_item, clear_item
- Activar otro item reemplaza el clima anterior (lluvia -> nieve, etc).
- Un item activo brilla (efecto encantado).
