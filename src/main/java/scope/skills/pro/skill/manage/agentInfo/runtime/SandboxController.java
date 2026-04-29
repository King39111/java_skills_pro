package scope.skills.pro.skill.manage.agentInfo.runtime;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@RestController
@RequestMapping("/sandbox")
public class SandboxController {

    // 用于存储 userId 和容器 ID 的映射关系
    private static final Map<String, String> userContainers = new ConcurrentHashMap<>();

    @PostMapping("/createContainer")
    public Map<String, Object> createContainer(@RequestBody(required = false) Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "default_user");
        log.info("收到创建沙箱容器请求, UserId: {}", userId);

        try {
            // 1. 调用本地 Docker 命令启动一个 Python 容器
            // 这里使用 python:3.9-slim 镜像，并保持容器后台运行
            String containerName = "sandbox-" + userId + "-" + UUID.randomUUID().toString().substring(0, 8);
            ProcessBuilder pb = new ProcessBuilder("docker", "run", "-d", "--name", containerName, "python:3.9-slim", "sleep", "infinity");
            Process process = pb.start();
            
            // 简单等待一下获取输出
            Thread.sleep(1000); 
            
            // 2. 记录容器 ID
            userContainers.put(userId, containerName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("containerId", containerName);
            return response;

        } catch (Exception e) {
            log.error("创建 Docker 容器失败", e);
            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("status", "error");
            errorResp.put("message", e.getMessage());
            return errorResp;
        }
    }

    /**
     * 模拟获取沙箱状态接口
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus(@RequestParam String userId, @RequestParam String sandboxId) {
        log.info("查询沙箱状态 - UserId: {}, SandboxId: {}", userId, sandboxId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("userId", userId);
        response.put("sandboxId", sandboxId);

        return response;
    }

    /**
     * 模拟启动沙箱接口
     */
    @PostMapping("/start")
    public Map<String, Object> startSandbox(@RequestBody(required = false) Object request) {
        log.info("收到启动沙箱请求: {}", request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "started");
        response.put("message", "Sandbox started");

        return response;
    }

    /**
     * 增加一个执行代码的接口示例
     */
    @PostMapping("/execute")
    public Map<String, Object> executeCode(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String code = (String) request.get("code");
        String containerId = userContainers.get(userId);

        if (containerId == null) {
            throw new RuntimeException("用户沙箱未初始化");
        }

        try {
            // 将代码写入容器并执行
            ProcessBuilder pb = new ProcessBuilder("docker", "exec", containerId, "python", "-c", code);
            Process process = pb.start();
            
            // 读取执行结果...
            // (此处省略读取 InputStream 的代码)
            
            Map<String, Object> response = new HashMap<>();
            response.put("output", "Code executed in container: " + containerId);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
