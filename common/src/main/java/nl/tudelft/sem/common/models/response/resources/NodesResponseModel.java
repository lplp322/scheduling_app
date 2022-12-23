package nl.tudelft.sem.common.models.response.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.Node;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodesResponseModel {
    Collection<Node> nodes;
}
