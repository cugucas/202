package cn.ac.iscas.common.response;

import java.util.List;

/**
 * @author LJian
 * @version 1.0
 * @description: TODO
 * @date 2022/10/31 10:35
 */
public class BatchCreateDbTableResponse extends BaseResponse{
    List<SingleDbTableResponse> success;
    List<SingleDbTableResponse> failed;

    public List<SingleDbTableResponse> getSuccess() {
        return success;
    }

    public void setSuccess(List<SingleDbTableResponse> success) {
        this.success = success;
    }

    public List<SingleDbTableResponse> getFailed() {
        return failed;
    }

    public void setFailed(List<SingleDbTableResponse> failed) {
        this.failed = failed;
    }
}
