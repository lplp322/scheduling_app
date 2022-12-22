package nl.tudelft.sem.resources.database;

import nl.tudelft.sem.resources.domain.resources.ResourceId;
import nl.tudelft.sem.resources.domain.resources.UsedResourcesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedResourceRepository extends JpaRepository<UsedResourcesModel, ResourceId> {

}
