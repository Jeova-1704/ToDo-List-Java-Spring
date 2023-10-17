package br.com.jeovabezerraleite.todolist.repositories;

import br.com.jeovabezerraleite.todolist.domain.tasks.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

    List<TaskModel> findByIdUser(UUID idUser);

}
