package br.com.jeovabezerraleite.todolist.controller;

import br.com.jeovabezerraleite.todolist.repositories.ITaskRepository;
import br.com.jeovabezerraleite.todolist.domain.tasks.TaskModel;
import br.com.jeovabezerraleite.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, @PathVariable UUID id,  HttpServletRequest servletRequest) {
        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        var idUser = servletRequest.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario não tem permisão para alterar está tarefa.");
        }

        Utils.copyNonNullPropeties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteTaskId(@PathVariable UUID id, HttpServletRequest servletRequest) {
        var task = this.taskRepository.findById(id).orElse(null);
        var idUser = servletRequest.getAttribute("idUser");

        if(task != null && !task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario não tem permisão para alterar está tarefa.");
        }
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().body("Tarefa deletada com sucesso");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
