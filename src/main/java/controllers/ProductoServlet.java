    package controllers;
    /*
     * Autor: Byron Melo
     * Fecha: 06/11/2025
     * Versión: 1.0
     * Descripción: Esta clase es un servlet que nos permitirá manejar peticiones HTTP tipo GET que el usuario
     * realice desde el navegador al dar click en un hipervínculo para mostrar un archivo html dinámico con
     * estructuras condicionales que muestran distintos datos de una tabla de productos solo si el usuario
     * se ha logeado, es decir solo si existe una sesión activa que se verifica comprobando si el contenedor
     * Optional<String> tiene un valor.
     * */
    import jakarta.servlet.ServletException;
    import java.io.IOException;

    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import java.io.PrintWriter;

    //Importamos las clases de nuestro package services
    import services.LoginService;
    import services.LoginServiceSessionImpl;
    import services.ProductoService;
    import services.ProductoServiceImpl;

    //Importamos el modelo de nuestra aplicación web
    import models.Producto;

    //Importamos la clase para crear listas
    import java.util.Arrays;
    import java.util.List;
    import java.util.Optional;

    /*Definimos una llave para acceder a este servlet
     */
    @WebServlet({"/productos.html", "/productos"})
    public class ProductoServlet extends HttpServlet {
        //Sobreescribimos el metodo GET
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            /*Creamos un objeto de tipo ProductosServices que hace referencia a la clase ProductosServicesImplement
             * el cual implementa el metodo, esto es polimorfismo ya que el objeto de tipo ProductoServices en
             * realidad es una instancia de ProductosServicesImplement.
             * Esto se hace así porque en Java es buena práctica programar contra interfaces, no contra clase, por lo
             * tanto, esto se hace siempre que queremos usar una clase que implementa un metodo de una interfaz.
             */
            ProductoService service = new ProductoServiceImpl();

            //Definimos una lista que usará el metodo listar de nuestro objeto services que ya implementó el metodo
            List<Producto> productos = service.listar();
            /*
            Crear una instancia de la clase LoginServiceSessionImpl que implementa el metodo de la interfaz
            LoginService
             */
            LoginService auth = new LoginServiceSessionImpl();

            /*Creamos un contenedor que guarda lo que retorna el metodo getUserName, que es el valor
             de nuestra clave username que es un atributo de un objeto HttpSession el cual creamos a partir
             la petición enviada por el cliente
             */
            Optional<String> usernameOptional = auth.getUsername(req);

            //Definimos el tipo de contenido que se enviará como respuesta a la petición
            resp.setContentType("text/html;charset=UTF-8");

            /*
             * Usamos un try-with-resources para que el objeto PrintWriter se cierre automáticamente
             * una vez se termine el bloque try
             * */
            try(PrintWriter out = resp.getWriter())
            {
                //Imprimimos un archivo HTML con PrintWriter
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                //Usamos UTF-8 para permitir caracteres especiales como tildes
                out.println("<meta charset=\"UTF-8\">");
                out.println("<title>Lista de Productos</title>");
                out.println("<link rel=\"stylesheet\" href=\"" + req.getContextPath() + "/css/estilos.css\">");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Lista de Productos</h1>");
                //Mostramos un mensaje si el usuario se logeo correctamente
                if (usernameOptional.isPresent()) {
                    out.println("<h3> Hola " + usernameOptional.get() + " Bienvenido!</h3>");
                }
                //Creamos un contenedor div para manejar la posición de la tabla
                out.println("<div>");
                //Creamos una tabla y definimos atributos para darle una presentación más agradable
                out.println("<table> ");
                /*
                 * Podríamos utilizar la etiqueta caption para poner un título que vaya unido a la tabla
                 * out.println("<caption style= 'text-align: center';>LISTA DE PRODUCTOS</caption>");
                 * */

                //Creamos la cabecera de la tabla
                out.println("<thead>");
                //Creamos la fila de la cabecera de la tabla que tendrá un color de fondo azul marino
                out.println("<tr>");
                //Creamos los campos de la cabecera de la tabla que representan los datos de cada producto
                out.println("<th>ID PRODUCTO</th>");
                out.println("<th>NOMBRE</th>");
                out.println("<th>TIPO</th>");
                //Se muestra el campo de precio solo si el usuario está logeado
                if(usernameOptional.isPresent()) {
                    out.println("<th>PRECIO</th>");
                    out.println("<th>OPCIONES</th>");
                }

                out.println("</tr>");
                out.println("</thead>");
                //Cuerpo de la tabla
                out.println("<tbody>");
                /*
                 * Creamos un bucle for-each para construir en cada iteración una fila con
                 * todos los datos de un producto con base a los atributos definidos en nuestro modelo
                 * */
                productos.forEach(p ->{
                    //El atributo align='center' nos permite centrar todos los datos de los campos de las filas
                    out.println("<tr>");
                    //Utilizamos los métodos públicos getters para acceder al valor de cada atributo de un producto
                    out.println("<td>" + p.getIdProducto() + "</td>");
                    out.println("<td>" + p.getNombre() + "</td>");
                    out.println("<td>" + p.getTipo() + "</td>");
                    if(usernameOptional.isPresent()) {
                        out.println("<td>" + p.getPrecio()+ "</td>");
                        /*
                        * Agregamos un hipervínculo a cada registro de la tabla productos, donde agregamos
                        * un parámetro en la URL que será el ID de cada producto que obtenemos con el metodo
                        * getIdProducto() de nuestra clase Producto
                        * */
                        out.println("<td><a href=\""
                        + req.getContextPath()
                        +"/agregar-carro?id=0"
                        +p.getIdProducto()
                        +"\">Agregar Producto al carro</a></td>");
                    }
                    out.println("</tr>");
                });
                out.println("</tbody>");
                out.println("</table>");
                out.println("</div>");
                out.println("<br>");
                //Hipervínculo para regresar al menú de productos
                out.println("<a href='" + req.getContextPath() + "/index.html'>Regresar</a>");
                out.println("</body>");
                out.println("</html>");

            }
        }
    }