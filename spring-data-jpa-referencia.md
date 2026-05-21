# Spring Data JPA — Referencia de Estudio
### Proyecto: e-commerce-lab

---

## 1. Entidades y Mapeo Básico

**`@Entity`** — marca una clase como entidad JPA. Hibernate la mapea a una tabla.

**`@Table(name = "...")`** — especifica el nombre de la tabla. Sin esto Hibernate usa el nombre de la clase.

**`@Id` + `@GeneratedValue`** — define la primary key y su estrategia de generación. `IDENTITY` delega al motor de DB (autoincrement en PostgreSQL).

**`@Column`** — personaliza la columna: `nullable`, `unique`, `length`, `precision`, `scale`. Sin esto Hibernate usa defaults.

**`@Enumerated(EnumType.STRING)`** — guarda el enum como texto en DB. Preferir siempre STRING sobre ORDINAL — si reordenas el enum, ORDINAL rompe los datos existentes.

---

## 2. Relaciones

### Regla de oro — El dueño de la relación
El dueño es quien tiene `@JoinColumn`. El lado con `mappedBy` **no es el dueño** y no controla la FK.
Sin `mappedBy` en `@OneToMany`, Hibernate crea una tabla intermedia innecesaria.

### `@ManyToOne`
- La FK vive en la tabla de esta entidad.
- Default fetch: **EAGER** — cambiarlo siempre a `LAZY`.
- Es el lado dueño de la relación.

### `@OneToMany`
- Siempre con `mappedBy` apuntando al campo `@ManyToOne` del otro lado.
- Default fetch: **LAZY**.
- Con `cascade = CascadeType.ALL` las operaciones se propagan a los hijos.
- ⚠️ No usar `CascadeType.REMOVE` en `@ManyToMany` — puede borrar entidades referenciadas por otros objetos.

### `@ManyToMany`
- No hay FK en ninguna tabla — se crea una tabla intermedia.
- El dueño define `@JoinTable` con el nombre de la tabla y columnas.
- El otro lado usa `mappedBy`.
- Default fetch: **LAZY**.

### Defaults de FetchType — pregunta clásica de entrevista
```
@ManyToOne  → EAGER  ← cambiar a LAZY siempre
@OneToMany  → LAZY
@ManyToMany → LAZY
```

---

## 3. Queries

### Tres formas de hacer queries

**Query Methods** — Spring Data genera el SQL por el nombre del método.
- `findByNameContainingIgnoreCase` → `LIKE '%x%'` sin distinción de mayúsculas.
- `findByPriceLessThan` → `WHERE price < ?`
- `findByCategoryOrderByPriceAsc` → con ORDER BY incluido.
- Límite: se vuelven ilegibles con muchos parámetros.

**JPQL con `@Query`** — orientado a objetos, trabaja con entidades no con tablas.
- Nunca escribes `category_id` — usas `p.category`.
- Portátil entre motores de DB.

**Native Query** — SQL puro, específico del motor.
- Usar solo cuando JPQL no alcanza.
- Rompe portabilidad entre DBs.
- Con paginación requiere `countQuery` adicional.

### Paginación con `Pageable`
`PageRequest.of(page, size, Sort.by("field").ascending())` — construye el objeto de paginación.
`Page<T>` retorna contenido + metadata: `totalElements`, `totalPages`, `pageNumber`.
`Page.map(fn)` — transforma el contenido manteniendo toda la metadata. Muy útil para convertir a DTO.

---

## 4. N+1 Problem

**Qué es:** en vez de 1 query, Hibernate ejecuta 1 + N — una para la lista y una adicional por cada elemento para cargar su relación LAZY.

**Por qué ocurre:** al acceder a una relación LAZY dentro de un loop, Hibernate hace una query individual por cada elemento.

**Cómo detectarlo:** activar `spring.jpa.show-sql=true` y contar las queries en los logs.

**Solución 1 — JOIN FETCH:**
```java
@Query("SELECT p FROM Product p JOIN FETCH p.category")
```
Genera un INNER JOIN — productos sin categoría no aparecen.

**Solución 2 — `@EntityGraph`:**
```java
@EntityGraph(attributePaths = {"category"})
List<Product> findAll();
```
Genera un LEFT JOIN — productos sin categoría sí aparecen con null.

**Diferencia clave entre ambas soluciones:**
- `JOIN FETCH` → INNER JOIN
- `@EntityGraph` → LEFT JOIN

---

## 5. `@Transactional`

**Por qué en la capa de servicio:** mantiene la sesión de Hibernate abierta durante toda la ejecución del método, permitiendo cargar relaciones LAZY sin `LazyInitializationException`.

**`readOnly = true`** — para métodos de solo lectura:
- Desactiva el **dirty checking** (Hibernate no rastrea cambios en entidades).
- Omite el flush al final.
- Mejora el performance en lecturas.

**Regla de rollback:**
- Rollback automático con `RuntimeException` y subclases (unchecked).
- NO hace rollback con `Exception` checked por default.
- Para forzarlo: `@Transactional(rollbackFor = Exception.class)`.

### Propagation — los más importantes

**`REQUIRED` (default)** — usa la transacción existente. Si no hay, crea una. Ideal para operaciones que deben ser atómicas con el llamador.

**`REQUIRES_NEW`** — siempre crea una transacción nueva, suspende la actual. Ideal para operaciones que deben persistir aunque el llamador haga rollback — audit logs, notificaciones, métricas.

---

## 6. Auditoría con Spring Data

**Setup necesario:**
1. `@EnableJpaAuditing` en la clase principal.
2. Clase base con `@MappedSuperclass` + `@EntityListeners(AuditingEntityListener.class)`.
3. Entidades extienden la clase base.

**`@MappedSuperclass`** — no crea tabla propia. Sus campos se mapean a las tablas de las subclases. A diferencia de `@Entity` que sí crea tabla propia.

**`@CreatedDate`** — se rellena automáticamente al hacer el primer `save()`. Usar con `updatable = false`.

**`@LastModifiedDate`** — se actualiza automáticamente en cada modificación.

**`@CreatedBy` / `@LastModifiedBy`** — requiere implementar `AuditorAware<T>` para proveer el usuario actual. En proyectos con Spring Security se obtiene del `SecurityContextHolder`.

---

## 7. Projections

**Para qué sirven:** evitar over-fetching — traer solo los campos necesarios en vez de la entidad completa.

**Interface Projection** — defines una interfaz con getters, Spring genera un proxy.
- Flexible pero puede tener problemas de serialización con Jackson.
- Requiere `@JsonSerialize` para serializar correctamente.

**Class Projection (DTO Projection)** — usas un record o clase con constructor expression en JPQL.
- `SELECT new com.pkg.MiDTO(p.id, p.name) FROM Product p`
- Más explícito, serializa sin problemas con Jackson.
- **Preferida en producción.**

---

## 8. Specifications

**Para qué sirven:** filtros dinámicos opcionales sin multiplicar query methods.

**Sin Specifications** el repositorio explota con combinaciones:
`findByName`, `findByCategory`, `findByNameAndCategory`, `findByNameAndPriceLessThan`...

**Con Specifications** un solo `findAll(spec)` acepta cualquier combinación construida dinámicamente.

**Requiere:** que el repositorio extienda `JpaSpecificationExecutor<T>`.

**Patrón:** cada criterio es un método estático que retorna `Specification<T>`. Se combinan con `.and()`, `.or()`, `.not()`.

Retornar `null` en una Specification significa "sin filtro" — Hibernate lo ignora automáticamente.

> Specifications usan Criteria API de JPA por debajo pero con una abstracción mucho más limpia y legible.

---

## 9. Optimistic Locking con `@Version`

**El problema:** dos usuarios leen el mismo registro, ambos modifican y el segundo sobreescribe al primero sin saberlo.

**La solución:** `@Version` agrega un campo que Hibernate incrementa en cada UPDATE. El WHERE incluye la versión — si no coincide, lanza `OptimisticLockException`.

**Optimistic vs Pessimistic:**
- **Optimistic** — no bloquea, detecta conflictos al guardar. Ideal para baja contención.
- **Pessimistic** — bloquea la fila con `SELECT FOR UPDATE`. Ideal cuando los conflictos son frecuentes y el costo de reintentar es alto.

---

## 10. Buenas Prácticas — Para Entrevista

**Las entidades JPA nunca deben salir de la capa de servicio.** El controller siempre trabaja con DTOs. Esto evita `LazyInitializationException`, evita exponer el modelo de datos y da control total sobre qué se retorna.

**Nunca retornar `Page<Entity>` desde el controller** — siempre `Page<DTO>`. Usar `Page.map(this::toDTO)`.

**`spring.jpa.open-in-view=false`** — desactivarlo siempre en APIs REST. Mantener la sesión abierta durante el renderizado es un anti-pattern.

**`javax.persistence` vs `jakarta.persistence`** — Spring Boot 2.x usa `javax`, Spring Boot 3.x usa `jakarta`. El cambio ocurrió cuando Oracle cedió Java EE a Eclipse Foundation y no podían usar el namespace `javax`.

---

## 11. Conceptos Clave para Memorizar

| Concepto | Para recordar |
|---|---|
| `mappedBy` | No soy el dueño — pregúntale al otro lado |
| N+1 | 1 query + 1 por cada elemento = desastre en producción |
| `JOIN FETCH` | INNER JOIN — excluye nulls |
| `@EntityGraph` | LEFT JOIN — incluye nulls |
| `readOnly=true` | Desactiva dirty checking — úsalo en lecturas |
| `REQUIRES_NEW` | Audit logs, notificaciones — sobreviven al rollback |
| `@MappedSuperclass` | Herencia de campos sin tabla propia |
| `@Version` | Control de concurrencia optimista |
| DTOs en controller | Regla de oro — entidades nunca salen del servicio |
| FetchType defaults | `@ManyToOne` EAGER, `@OneToMany` LAZY |
