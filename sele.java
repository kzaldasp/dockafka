import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.StaleElementReferenceException
import java.time.Duration

// Definir la lista de productos
def lst_productos = [
    "102535", "102536", "102537", "104870", "104874", "104875", 
    "105835", "105836", "102534", "103001", "104873", "104876", 
    "104877", "105833", "102538", "104871"
]

// 1. Navegar a la URL
WDS.browser.get('http://localhost:8080/appFacturacionWeb/faces/login.xhtml')

// 2. Ingresar caracteres en los campos de texto
def user_input = WDS.browser.findElement(By.id('j_idt5:j_idt11'))
user_input.sendKeys('juan')

def password_input = WDS.browser.findElement(By.id('j_idt5:j_idt13'))
password_input.sendKeys('vendedor')

def login_button = WDS.browser.findElement(By.id('j_idt5:j_idt14'))
login_button.click()

def wait = new WebDriverWait(WDS.browser, Duration.ofSeconds(10))

// Esperar a que el botón "Generar Factura" esté visible y hacer clic
def btn_Generar_Factura = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id('form2:j_idt7')))
btn_Generar_Factura.click()

// Reubicar el elemento span antes de interactuar con él
def span_element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector('span.ui-icon-triangle-1-s')))
span_element.click()

def random = new Random()
def veces = random.nextInt(5) + 1  // Esto generará un número entre 1 y 5

// Generar un número aleatorio entre 0 y 41
def randomIndex = new Random().nextInt(42) // 42 porque el rango es [0, 41]

// Esperar hasta que el elemento del dropdown sea visible y hacer clic
def dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form2:j_idt15_${randomIndex}")))
dropdown.click()

// Generar un número aleatorio para el número de productos a adicionar
def vec = new Random().nextInt(5)

for (int i = 0; i < vec; i++) {
    def attempts = 0
    def maxRetries = 3
    def select_producto = null

    // Intentar obtener el elemento y manejar el error de "stale element"
    while (attempts < maxRetries) {
        try {
            // Reubicar el elemento en cada intento
            select_producto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form3:j_idt24")))
            break // Salir del bucle si se obtiene correctamente
        } catch (StaleElementReferenceException e) {
            attempts++
            Thread.sleep(500) // Esperar antes de intentar de nuevo
        }
    }

    if (select_producto != null) {
        // Volver a buscar el elemento de selección antes de usarlo
        def select_element = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form3:j_idt24"))))
        def random_producto = lst_productos[random.nextInt(lst_productos.size())]
        select_element.selectByValue(random_producto)

        // Localizar el campo de cantidad por nombre y enviar un número aleatorio entre 0 y 5
        def cant = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name('form3:j_idt28')))
        cant.clear() // Limpiar el campo antes de enviar nuevos datos
        cant.sendKeys(String.valueOf(new Random().nextInt(6)))

        // Localizar y hacer clic en el botón adicionar
        def btn_adicionar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form3:j_idt29")))
        btn_adicionar.click()
    }
}
