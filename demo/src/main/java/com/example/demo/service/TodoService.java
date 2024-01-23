package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class TodoService {
	@Autowired
	private TodoRepository repository;
	public String testService() {
		// Todo Entity 생성
		TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
		// Todo Entity 저장
		repository.save(entity);
		// TodoEntity 검색
		TodoEntity savedEntity = repository.findById(entity.getId()).get();
		return savedEntity.getTitle();
	}
	public List<TodoEntity> create(final TodoEntity entity) {
		// Validations
		validate(entity);
		
		repository.save(entity);
		log.info("Entity Id : {} is saved.", entity.getId());
		return repository.findByUserId(entity.getUserId());
	}
	private void validate(final TodoEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannot be null.");
		}
		
		if(entity.getUserId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}
	}
	public List<TodoEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}
	public List<TodoEntity> update(final TodoEntity entity) {
		// (1) 유효성 검사
		validate(entity);
		
		// (2) id를 사용해 entity 가져옴
		final Optional<TodoEntity> original = repository.findById(entity.getId());
		
		if(original.isPresent()) {
			// (3) 반환된 todoEntity가 존재하면 값을 새 entity의 값으로 덮어 씌운다.
			final TodoEntity todo = original.get();
			todo.setId(entity.getId());
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.isDone());

			// (4) 데이터 베이스에 새 값을 저장한다.
			repository.save(todo);
		}
		
		// (5) RetrieveTodo에서 만든 메서드를 사용해 유저으 모든 TodoList 리턴
		return retrieve(entity.getUserId());
	}
}
