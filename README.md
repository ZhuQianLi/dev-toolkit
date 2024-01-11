# 工作中常用的代码生成工具

### 插件安装方式

* 下载最新Releases里的dev-toolkit-********.zip文件，在idea中安装。

### 已实现功能

  工作中常用的代码生成工具<br/>
1. 生成建表语句<br/>
   * 使用方式：右键带有@Entity注解的类名，选择Generate菜单<br/>
   * 支持`@Nullable`<br/>
   * 支持`@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "outerId"})})`<br/>
   * 支持`@GeneratedValue(strategy = GenerationType.IDENTITY)`<br/>
   * 支持`@Type(type = "Json")`<br/>
   * 支持`代码注释`生成comment<br/>
2. 生成相关查询<br/>
   * 使用方式：右键Dao结尾的类名，选择Generate菜单<br/>
   * 支持findByNameAndAge<br/>
   * 支持@Query<br/>
   * 支持deleteBy<br/>
   * 支持OrderById<br/>
3. 实现对象转换方法<br/>
   * 使用方式：右键转换方法名，选择Generate菜单<br/>
   * 根据方法的返回值和参数，生成赋值代码
   * 匹配规则：名称一样
   * 支持多个参数，按参数顺序匹配
4. 添加@NotNull、@Valid注解<br/>
   * 使用方式：右键Cmd结尾的类名，选择Generate菜单<br/>
   * 支持过滤已经打上@Nullable、@NotNull的字段<br/>
   * 支持自动增加@Valid<br/>
5. Class生成ts类型代码<br/>
   * 使用方式：右键类名，选择Generate菜单<br/>