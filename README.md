# dev-toolkit
工作中常用的代码生成工具<br/>
1. Entity生成表结构<br/>
  * 使用方式：右键带有@Entity注解的类名，选择Generate菜单<br/>
  * 支持`@Nullable`<br/>
  * 支持`@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "outerId"})})`<br/>
  * 支持`@GeneratedValue(strategy = GenerationType.IDENTITY)`<br/>
  * 支持`@Type(type = "Json")`<br/>
2. EntityDao生成相关查询语句<br/>
  * 使用方式：右键Dao结尾的类名，选择Generate菜单<br/>
  * 支持findByNameAndAge<br/>
  * 支持@Query<br/>
