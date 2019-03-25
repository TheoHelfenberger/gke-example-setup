package ch.thelf.demo.gke.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ch.thelf.demo.gke.microservice.domain.Visitor;

@RepositoryRestResource
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
	// ignore
}
