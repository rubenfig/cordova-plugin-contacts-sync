# Contacts Sync

##init

Inicializa la cuenta del SyncAdapter para realizar la synchronización de contactos y agregar el botón de acción.

###Ejemplo:

navigator.contactssync.init({
            url: "url.sincronizacion/contactos",
            accountName: "Contactos",
            appName: "Aplicación",
            message: "Mensaje a mostrar en la acción"
          },function (result) {
            console.log(result);
          });