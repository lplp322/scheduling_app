package nl.tudelft.sem.resources.database;

import nl.tudelft.sem.resources.domain.resources.ResourceAllocationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocationModel, String> {


}
