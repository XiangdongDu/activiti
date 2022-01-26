package boot.spring.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("参数配置表")
public class ParamItem implements Serializable {
    @ApiModelProperty("主键")
    Integer id;

    @ApiModelProperty("参数类型")
    String param_name;

    @ApiModelProperty("参数名称")
    String item_name;

    @ApiModelProperty("参数描述")
    String item_info;

    @ApiModelProperty("参数属性值")
    String item_attr;

    @ApiModelProperty("参数状态（1：正常；0：失效）")
    String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_info() {
        return item_info;
    }

    public void setItem_info(String item_info) {
        this.item_info = item_info;
    }

    public String getItem_attr() {
        return item_attr;
    }

    public void setItem_attr(String item_attr) {
        this.item_attr = item_attr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
