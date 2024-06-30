# Instalación de dependencias

## Contenido
* [Java en Windows](#instalación-de-java-en-windows-usando-sdkman)
* [Java en Mac](#instalación-de-java-en-mac-usando-sdkman)
* [Maven en Windows](#instalación-de-maven-en-windows)
* [Maven en Mac](#instalación-de-maven-en-mac)
* [Git en Windows](#instalación-de-git-en-windows)
* [Git en Mac](#instalación-de-git-en-windows)
* [Postman](https://www.postman.com/downloads/)

===========================================================

## Instalación de Java en Windows usando SDKMAN

### Instrucciones Paso a Paso

1. **Instalar SDKMAN**

   **Abrir una Terminal o Símbolo del sistema**:
    - Presiona `Windows + R`, escribe `cmd` y presiona `Enter`.

   **Instalar SDKMAN**:
    - Ejecuta el siguiente comando para descargar e instalar SDKMAN:
      ```bash
      curl -s "https://get.sdkman.io" | bash
      ```

2. **Sigue las Instrucciones en Pantalla**:
    - El proceso de instalación te guiará para agregar SDKMAN! a las variables de entorno de tu sistema.

3. **Reinicia tu Terminal**:
    - Cierra y vuelve a abrir tu terminal, o ejecuta el siguiente comando para inicializar SDKMAN:
      ```bash
      source "$HOME/.sdkman/bin/sdkman-init.sh"
      ```

4. **Verifica la Instalación de SDKMAN**:
    - Comprueba si SDKMAN! está instalado correctamente ejecutando:
      ```bash
      sdk version
      ```

### Instalación y Gestión de Versiones de Java

1. **Listar Versiones de Java Disponibles**:
    - Para ver todas las versiones de Java que se pueden instalar, ejecuta:
      ```bash
      sdk list java
      ```

2. **Instalar Corretto 11**:
    - Para instalar una versión específica de Corretto 11 (por ejemplo, 11.0.23-amzn), ejecuta:
      ```bash
      sdk install java 11.0.23-amzn
      ```

3. **Establecer Corretto 11 como la Versión Activa**:
    - Para usar la versión instalada de Corretto 11, ejecuta:
      ```bash
      sdk use java 11.0.23-amzn
      ```

4. **Establecer Corretto 11 como la Versión Predeterminada**:
    - Para establecer Corretto 11 como la versión predeterminada de Java, ejecuta:
      ```bash
      sdk default java 11.0.23-amzn
      ```

5. **Verificar la Versión de Java**:
    - Asegúrate de que se esté utilizando la versión correcta de Java ejecutando:
      ```bash
      java -version
      ```

===========================================================

## Instalación de Java en Mac usando SDKMAN

### Instalación de SDKMAN

1. **Abrir la Terminal** e instalar SDKMAN! ejecutando el siguiente comando:
   ```bash
   curl -s "https://get.sdkman.io" | bash
    ```
2. Sigue las instrucciones en pantalla para completar la instalación. Esto generalmente implica agregar SDKMAN! a las variables de entorno de tu sistema.

3. Reinicia tu terminal o ejecuta el siguiente comando para cargar SDKMAN!:
   ```bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
    ```

4. Verifica la instalación ejecutando:
   ```bash
   sdk version
    ```

## Instalación y Gestión de Versiones de Java

1. **Listar las versiones de Java disponibles**:
   ```bash
   sdk list java
   ```
2. Instalar Corretto 11:
   ```bash
   sdk install java 11.0.23-amzn
   ```
3. Establecer Corretto 11 como la versión activa:
   ```bash
   sdk use java 11.0.23-amzn
   ```
4. Establecer Corretto 11 como la versión predeterminada:
   ```bash
   sdk default java 11.0.23-amzn
   ```
5. Verificar la versión de Java:
   ```bash
   java -version
   ```


# Instalación de Maven en Windows

## 1. Descargar Maven

### Abrir el Navegador:
Ve a la página oficial de descargas de Maven: [Apache Maven Download](https://maven.apache.org/download.cgi)

### Descargar el Archivo Binario:
En la sección "Files", encuentra el enlace para descargar el archivo binario de Maven (generalmente un archivo `.zip`). Haz clic en el enlace para descargarlo.

## 2. Instalar Maven

### Extraer el Archivo Zip:
Navega a la carpeta donde descargaste el archivo `.zip` de Maven y extráelo en una ubicación adecuada (por ejemplo, `C:\Program Files\Apache\maven`).

### Configurar las Variables de Entorno:

1. Abre el **Panel de Control** y ve a `Sistema y Seguridad > Sistema > Configuración avanzada del sistema`.
2. Haz clic en el botón `Variables de entorno`.
3. En la sección `Variables del sistema`, busca la variable `Path` y selecciónala. Luego, haz clic en `Editar`.
4. Añade la ruta al directorio `bin` de Maven a la variable `Path`. Por ejemplo, si instalaste Maven en `C:\Program Files\Apache\maven`, añade `C:\Program Files\Apache\maven\bin`.

### Crear una Nueva Variable de Entorno M2_HOME:

1. En la sección `Variables del sistema`, haz clic en `Nueva` para crear una nueva variable de entorno.
2. Nombra la variable como `M2_HOME` y establece su valor como la ruta al directorio donde instalaste Maven (por ejemplo, `C:\Program Files\Apache\maven`).

### Crear una Nueva Variable de Entorno MAVEN_HOME:

1. Del mismo modo, crea otra nueva variable de entorno llamada `MAVEN_HOME` y establece su valor como la ruta al directorio donde instalaste Maven (por ejemplo, `C:\Program Files\Apache\maven`).

## 3. Verificar la Instalación de Maven

### Abrir una Terminal o Símbolo del sistema:
Presiona `Windows + R`, escribe `cmd` y presiona `Enter`.

### Verificar la Versión de Maven:
Ejecuta el siguiente comando para asegurarte de que Maven se haya instalado correctamente:
```bash
mvn -version
```

# Instalación de Maven en Mac

## Paso a Paso

### 1. Instalar Homebrew

#### Abrir la Terminal:
Puedes abrir la Terminal desde `Aplicaciones > Utilidades > Terminal` o buscando "Terminal" en Spotlight (`Cmd + Espacio`).

#### Instalar Homebrew:
Ejecuta el siguiente comando para instalar Homebrew:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

#### Seguir las Instrucciones en Pantalla:
El script de instalación te guiará a través de los pasos necesarios para completar la instalación, incluyendo la configuración de las variables de entorno.

#### Verificar la Instalación de Homebrew:
Una vez completada la instalación, verifica que Homebrew se haya instalado correctamente ejecutando:

```bash
brew --version
```

### Instalar Maven utilizando Homebrew
Actualizar Homebrew (opcional pero recomendado):
Ejecuta el siguiente comando para asegurarte de que Homebrew está actualizado:

```bash
brew update
```

#### Instalar Maven:

```bash
brew install maven
```

#### Verificar la Instalación de Maven:

```bash
mvn -version
```

# Instalación de Git en Windows

## Paso a Paso

### 1. Descargar Git

#### Abrir el Navegador:
Ve a la página oficial de descargas de Git: [Git Downloads](https://git-scm.com/download/win)

#### Descargar el Instalador:
Haz clic en el enlace para descargar el instalador de Git para Windows.

### 2. Instalar Git

#### Ejecutar el Instalador:
Una vez que se haya descargado el archivo `.exe`, haz doble clic en él para iniciar el proceso de instalación.

#### Seguir las Instrucciones del Instalador:
Sigue las instrucciones en pantalla. Puedes aceptar las opciones predeterminadas o personalizar la instalación según tus preferencias.

**Seleccionar el Editor de Texto por Defecto:** Durante la instalación, se te pedirá que selecciones un editor de texto. Puedes elegir el editor que prefieras (por ejemplo, Vim, Notepad++, VSCode).

**Ajustar la Variable de Entorno PATH:** Asegúrate de seleccionar la opción para utilizar Git desde la línea de comandos y también desde el software de terceros.

**Seleccionar el Manejador de HTTPS:** Puedes optar por usar el OpenSSL Library para manejar HTTPS.

**Configuración de la Línea de Comandos:** Puedes elegir usar la terminal MinTTY o la consola de Windows.

**Opciones adicionales:** Puedes habilitar opciones adicionales como la integración con el Explorador de Windows y Git LFS (Large File Support).

### 3. Verificar la Instalación de Git

#### Abrir la Terminal o Símbolo del sistema:
Presiona `Windows + R`, escribe `cmd` y presiona `Enter`.

#### Verificar la Versión de Git:
Ejecuta el siguiente comando para asegurarte de que Git se haya instalado correctamente:
```bash
git --version
```

#### Configurar nombre de usuario
```bash
git config --global user.name "Tu Nombre"
```

#### Configuración de correo
```bash
git config --global user.email "tu.email@ejemplo.com"
```

# Instalación de Git en Mac

### 1. Instalar Git

Ejecuta el siguiente comando para instalar Git utilizando Homebrew:
```bash
brew install git
```

### 2. Verificar la Instalación de Git
```bash
git --version
```