package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Creator: Runge
 * Date: 2018/4/20
 * Time: 15:43
 * Description:
 */
public class DevopsIngressPathVO {
    @ApiModelProperty("Ingress的path值")
    private String path;
    @ApiModelProperty("path对应的网络id")
    private Long serviceId;
    @ApiModelProperty("网络名称")
    private String serviceName;
    @ApiModelProperty("网络状态")
    private String serviceStatus;
    @ApiModelProperty("网络端口")
    private Long servicePort;

    public DevopsIngressPathVO() {
    }

    /**
     * 构造函数
     */
    public DevopsIngressPathVO(String path, Long serviceId, String serviceName, String serviceStatus) {
        this.path = path;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceStatus = serviceStatus;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Long getServicePort() {
        return servicePort;
    }

    public void setServicePort(Long servicePort) {
        this.servicePort = servicePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DevopsIngressPathVO that = (DevopsIngressPathVO) o;
        return Objects.equals(path, that.path)
                && Objects.equals(serviceId, that.serviceId)
                && Objects.equals(servicePort, that.servicePort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, serviceId);
    }
}
