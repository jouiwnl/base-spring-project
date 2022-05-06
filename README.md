# Base Java Spring boot Project with an Dynamic Dto builder it helps to customize json responses!

## Pay attention!
All Entity classes needs to implement a DatabaseEntity<{ Type of your Entity id (Long, Integer or UUID) }>.

After that, build your json this way (Simples ResponseEntity Controller):
```
public ResponseEntity<DynamicDto> findAll() {
  Employee employee = new Employee("João Henrique", 12345678910, "Male", "SC");
  
  DynamicDto dto = 
    DynamicDto.of(employee)
      .with("name", employee.getName())
      .with("cpf", employee.getCpf())
      .with("gender", employee.getGender())
      .with("state", employee.getState());
      
  return ResponseEntity.ok(dto);
}
```

## 🚀 Please, enjoy!

⌨️ with ❤️ by [João Henrique](https://github.com/jouiwnl) 😊
