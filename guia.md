# Guía de instalación de Apache Kafka

## 1. Configuración de Red

### Paso 1: Actualizar el sistema a la última versión de la lista de paquetes

1. Abre una terminal.
2. Escribe los siguientes comandos y presiona Enter:

    ```bash
    sudo dnf check-update
    sudo dnf install dnf-utils
    sudo dnf install epel-release
    ```


- `sudo dnf check-update`: Verifica si hay actualizaciones disponibles para los paquetes instalados en el sistema.
- `sudo dnf install dnf-utils`: Instala utilidades adicionales para el gestor de paquetes DNF.
- `sudo dnf install epel-release`: Instala el repositorio EPEL (Extra Packages for Enterprise Linux), que proporciona paquetes adicionales no disponibles en los repositorios estándar.

### Step 2. Installing Java.
Apache Kafka es una aplicación basada en Java, por lo que Java debe estar instalado en su servidor. Ahora ejecute el siguiente comando a continuación para instalar Java en su sistema:

1. Ejecutar
    ```bash
    sudo dnf install java-11-openjdk
    ```
2. Verificar que se instalo correctamente:
    ``` bash
    java --version
    ```
### Step 3.Usuario para Apache Kafka.
Antes de crear un nuevo usuario tenemos que crear sus directorios, en este caso vamos a ocupar opt y kafka.

1. Creación de directorios
    ```bash
    sudo mkdir /opt
    sudo mkdir /opt/kafka
    ```
2. Crear nuevo usuario
    ```bash
    sudo useradd -r -d /opt/kafka -s /usr/sbin/nologin kafka
    ```
    Este comando crea un nuevo usuario del sistema llamado `kafka`. La opción `-r` indica que el usuario es del tipo de sistema (sin capacidad de inicio de sesión), `-d /opt/kafka` especifica el directorio principal del usuario como `/opt/kafka`, y `-s /usr/sbin/nologin` establece la shell de inicio de sesión del usuario como `nologin`, lo que impide que el usuario inicie sesión en el sistema.
### Step 4. Instalación de  Apache Kafka.


1. Descarga del archivo.

    ``` bash
    sudo curl -fsSLo kafka.tgz https://downloads.apache.org/kafka/3.3.1/kafka_2.12-3.3.1.tgz
    ```
    Utilizamos `curl` como herramienta para la transferencia de datos desde o hacia un servidor. La opción `-f` activa el modo de fallo silencioso para que no se muestren errores de descarga, `-s` silencia el progreso de la descarga, `-S` muestra mensajes de error si los hay, `-L` sigue redirecciones, y `-o` guarda el archivo descargado con el nombre que seleccionemos, en este caso, `kafka.tgz`.
3. Extraemos y movemos los archivos a nuestra carpeta del usuario.

     ``` bash
    tar -xzf kafka.tgz
    sudo mv kafka_2.12-3.3.1/* /opt/kafka
    ```
3. Cambia el propietario y grupo de `/opt/kafka` y todos sus contenidos a `kafka`, de forma recursiva.

     ``` bash
    sudo chown -R kafka:kafka /opt/kafka
    ```
4. Creación de un directorio para guardar los logs.

     ``` bash
    sudo -u kafka mkdir -p /opt/kafka/logs
    ```
5. Editar el archivo `server.properties`.

     ``` bash
    sudo -u kafka nano /opt/kafka/config/server.properties
    ```
    Buscamos la linea `log.dirs` y ponemos la ruta del directorio que creamos. 
    
     ``` 
    log.dirs=/opt/kafka/logs
    ```
### Step 5. Creación de Systemd para Apache Kafka y Zookeper

1. Creamos el servico de `Zookeper` en `systemd`.

    ```bash
    sudo nano /etc/systemd/system/zookeeper.service
    ```

    Añadimos las siguientes isntrucciones.

    ```
    [Unit]
    Requires=network.target remote-fs.target
    After=network.target remote-fs.target

    [Service]
    Type=simple
    User=kafka
    ExecStart=/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties
    ExecStop=/opt/kafka/bin/zookeeper-server-stop.sh
    Restart=on-abnormal

    [Install]
    WantedBy=multi-user.target
    ```
2. Creamos el servico de `Apache Kafka` en `systemd`.

    ```bash
    sudo nano /etc/systemd/system/kafka.service
    ```
    
    Añadimos las siguientes isntrucciones.
p
    ```
    [Unit]
    Requires=zookeeper.service
    After=zookeeper.service

    [Service]
    Type=simple
    User=kafka
    ExecStart=/bin/sh -c '/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties > /opt/kafka/logs/start-kafka.log 2>&1'
    ExecStop=/opt/kafka/bin/kafka-server-stop.sh
    Restart=on-abnormal

    [Install]
    WantedBy=multi-user.target
    ```
3. Guardamos e iniciamos nuestros nuevos servicios.

    ```bash
    sudo systemctl daemon-reload
    sudo systemctl start zookeeper
    sudo systemctl start kafka  
    ```

    Recargamos la configuración de `systemd` para aplicar cualquier cambio reciente en los archivos de servicio.

    Iniciamos el servicio de Zookeeper utilizando `systemd`.

    Iniciamos el servicio de Kafka utilizando `systemd`.
        