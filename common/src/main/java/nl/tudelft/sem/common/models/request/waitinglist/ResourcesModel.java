package nl.tudelft.sem.common.models.request.waitinglist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesModel {
    private int cpu;
    private int gpu;
    private int ram;
}
