package br.com.jeovabezerraleite.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));

        var currenteDate = LocalDateTime.now();
        if(currenteDate.isAfter(taskModel.getStartAt()) || currenteDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio ou termino deve ser maior do que a data atual.");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicial deve ser menor do que a data de termino.");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }

    @GetMapping("/")
    public List<TaskModel> listAllTasks(HttpServletRequest servletRequest) {
        return this.taskRepository.findByIdUser((UUID)servletRequest.getAttribute("idUser"));

    }

    @PutMapping("/{id}")
    public TaskModel updateTask(@RequestBody TaskModel taskModel, @PathVariable UUID id,  HttpServletRequest servletRequest) {
        var idUser = servletRequest.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);
        return this.taskRepository.save(taskModel);
    }

}
