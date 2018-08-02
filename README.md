# Contacts Sync

##init

Inicializa la cuenta del SyncAdapter para realizar la synchronización de contactos y agregar el botón de acción.

###Ejemplo:

navigator.contactssync.init({
            url: "http://10.150.16.123:8180/sync-contactos-1.0/api/contactos",
            accountName: "Contactos",
            appName: "Billetera Personal",
            message: "Transferir a "
          },function (result) {
            console.log(result);
          });