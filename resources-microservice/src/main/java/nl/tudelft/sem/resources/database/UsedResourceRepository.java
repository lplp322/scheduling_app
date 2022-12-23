package nl.tudelft.sem.resources.database;

import nl.tudelft.sem.resources.domain.resources.ResourceId;
import nl.tudelft.sem.resources.domain.resources.UsedResourcesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface UsedResourceRepository extends JpaRepository<UsedResourcesModel, ResourceId> {
    Collection<UsedResourcesModel> findAllByDate(LocalDate date);
}
