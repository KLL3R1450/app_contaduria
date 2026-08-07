# Reglas de Negocio: Proyecto_Despacho

Este documento recopila las reglas de negocio, relaciones de dominio y validaciones implementadas en el sistema de contaduría.

---

## 👥 1. Entidades y Jerarquía de Personas

El sistema cuenta con una clase base abstracta `Personas` que define los datos básicos de identificación:
* **Identificador único (`id_persona`)**
* **Nombre**
* **RFC**
* **Código Postal (CP)**
* **Correo electrónico**
* **Regímenes Fiscales (`idsRegimenes`)**: Lista de identificadores de regímenes tributarios asociados a la persona.

Tanto **Cliente** como **Terceros** heredan de `Personas`.

---

## 👔 2. Relación de Contadores y Clientes

* **Contador (`Contadores`)**: 
  * Es el usuario del despacho que gestiona una cartera de clientes.
  * Atributos: `id_contador`, `nombre`, `contacto`, e `idsClientes` (lista de clientes a su cargo).
* **Asignación**: Cada `Cliente` tiene asignado exactamente un `id_contador`.
* **Honorarios**: Los clientes tienen un atributo `honorarios` que representa el cobro acordado con el despacho por sus servicios.

---

## 📜 3. Regímenes Fiscales (`Regimenes`)

* Un régimen fiscal representa el esquema tributario bajo el cual tributa una persona (física o moral).
* **Asociación**: Tanto los **Clientes** como los **Terceros** pueden estar dados de alta en múltiples regímenes simultáneamente.
* **Operaciones**: Se pueden añadir (`addRegimenACliente`, `agregarRegimenTercerro`) o retirar (`deleteRegimenACliente`, `borrarRegimenTerero`) regímenes en cualquier momento.

---

## 🔑 4. Firmas Electrónicas (`EFirmas`)

La e-firma es indispensable para los trámites fiscales de cada cliente:
* Cada cliente puede tener una única e-firma asignada en la base de datos.
* Se almacenan dos fechas clave:
  1. **Fecha de Expiración (`fecha_expiracion`)**: Fecha límite en la que la firma actual es válida.
  2. **Fecha de Renovación (`fecha_renovacion`)**: Fecha planeada o registrada de su renovación.
* Existe una funcionalidad específica para renovar estas fechas (`renovarFirma`).

---

## 📊 5. Declaraciones Mensuales (`Declaracion`)

El sistema gestiona la contabilidad mensual y anual de cada cliente mediante declaraciones:
* **Campos Clave**: `id_cliente`, `anio` (año) y `mes`.
* **Seguimiento Contable**:
  * **Gastos (`gastos`)**: Indica si se han cargado/declarado los gastos del mes (0 o 1).
  * **Ingresos (`ingresos`)**: Indica si se han cargado/declarado los ingresos del mes (0 o 1).
* **Estado de Declarado (`declarado`)**:
  * Indica si la declaración general del mes se encuentra completada y presentada (0 o 1).
  * Se puede marcar como declarado (`setDeclarado`) o revertir el estado (`desDeclarar`).

---

## 🤝 6. Relación con Terceros (`Terceros`)

* Un `Tercero` representa un proveedor, cliente u otra parte relacionada con la cual opera un cliente del despacho.
* **Multi-relación**: Un tercero puede estar asociado a múltiples clientes del despacho (`insertTercero`, `relacionarClientes`).

---

## 📝 7. Validaciones de Datos

Implementadas en la clase `utils.Validator`, se aplican las siguientes expresiones regulares para garantizar la integridad de los datos ingresados:

1. **Contacto/Teléfono**: Debe ser una cadena numérica de exactamente 10 dígitos.
   * *Expresión*: `^\d{10}`
2. **Correo Electrónico**: Formato estándar de email.
   * *Expresión*: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`
3. **Código Postal**: Cadena numérica de exactamente 5 dígitos.
   * *Expresión*: `^[0-9]{5}$`
4. **RFC (Registro Federal de Contribuyentes - México)**: Formato para personas físicas (4 letras) y morales (3 letras), seguido de fecha AAMMDD y homoclave de 3 caracteres.
   * *Expresión*: `^[A-ZÑ&]{3,4}\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])[A-Z0-9]{3}$`
