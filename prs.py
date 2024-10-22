from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
import time
import random

# Configurar las opciones del navegador (sin modo headless)
options = Options()

# Inicializar el servicio de GeckoDriver
service = Service("/usr/local/bin/chromedriver")

# Inicializar el driver de Firefox
driver = webdriver.Chrome(service=service, options=options)

# Codigos de productos
lst_productos = [
    "102535", "102536", "102537", "104870", "104874", "104875", 
    "105835", "105836", "102534", "103001", "104873", "104876", 
    "104877", "105833", "102538", "104871"
] 
try:
    driver.implicitly_wait(100)

    # Abre la página web
    driver.get('http://localhost:8080/appFacturacionWeb/faces/login.xhtml')

    for i in range (3):

        wait = WebDriverWait(driver, 20)  # Aumentar el tiempo de espera a 20 segundos

        user_input = wait.until(EC.presence_of_element_located((By.ID, 'j_idt5:j_idt11')))
        user_input.clear()
        user_input.send_keys('juan')

        password_input = driver.find_element(By.ID, 'j_idt5:j_idt13')
        password_input.clear()
        password_input.send_keys('vendedor')

        login_button = driver.find_element(By.ID, 'j_idt5:j_idt14')
        login_button.click()

        btn_Generar_Factura = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.ID, "form2:j_idt7"))
        )

        btn_Generar_Factura.click()

        span_element = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, "span.ui-icon-triangle-1-s"))
        )

        span_element.click()
        # Esperar a que el dropdown esté disponible
        dropdown = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.ID, "form2:j_idt15_{}".format(random.randint(0, 41))))
        )

        dropdown.click()

            

        

        for i in range(random.randint(1, 5)):
            
            Select_producto = WebDriverWait(driver, 10).until(
                EC.visibility_of_element_located((By.ID, "form3:j_idt24"))
            )

            select_element = Select(Select_producto)

            select_element.select_by_value(random.choice(lst_productos))



            password_input = driver.find_element(By.NAME, 'form3:j_idt28')
            password_input.send_keys(random.randint(0, 5))

            btn_adicionar = WebDriverWait(driver, 10).until(
                EC.visibility_of_element_located((By.ID, "form3:j_idt29"))
            )

            btn_adicionar.click()


        btn_guardar = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.NAME, "form2:j_idt19"))
        )

        btn_guardar.click()
        driver.get('http://localhost:8080/appFacturacionWeb/faces/login.xhtml')

    time.sleep(4)
finally:
    driver.implicitly_wait(5)
    driver.quit()
