package scope.skills.pro.skill.manage.agentInfo.entity;

import lombok.Data;

import java.util.List;

@Data
public class ProductInfo {
    public String name;
    public Double price;
    public List<String> features;

    public ProductInfo() {}  // 必须有无参构造函数
}