package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.stereotype.Service;

public abstract class ResourceLogicUtils {

    /** Adds resources from inputs and returns their sum.
     *
     * @param resources1 resource input
     * @param resources2 resource input
     * @return sum of resources1 and resources2
     */
    public static ResourcesModel addResources(ResourcesModel resources1, ResourcesModel resources2) {
        if (resources1 == null) {
            return resources2;
        }
        if (resources2 == null) {
            return resources1;
        }
        return new ResourcesModel(resources1.getCpu() + resources2.getCpu(),
                                  resources1.getGpu() + resources2.getGpu(),
                                  resources1.getRam() + resources2.getRam());
    }

    /** Subtracts "by" from "from" and returns their difference.
     *
     * @param from resources to be subtracted from.
     * @param by resources to be subtracted.
     * @return the difference of "from" and "by"
     */
    public static ResourcesModel subtractResources(ResourcesModel from, ResourcesModel by) {
        if (from == null) {
            return null;
        }
        if (by == null) {
            return from;
        }
        ResourcesModel res = new ResourcesModel(from.getCpu() - by.getCpu(),
                from.getGpu() - by.getGpu(), from.getRam() - by.getRam());
        if(!checkValidity(res))
            return null;
        return res;
    }

    public static boolean checkValidity(ResourcesModel resources) {
        return resources.getRam() >= 0 && resources.getCpu() >= 0 && resources.getGpu() >= 0
                && resources.getGpu() <= resources.getCpu() && resources.getRam() <= resources.getCpu();
    }

    /** Checks if resources can be released.
     *
     * @param allocatedResources Resources allocated to the faculty.
     * @param usedResources Resources used by that faculty on the given date.
     * @param releasedResources Resources to be released from that faculty.
     * @return true if resources can be released, false otherwise.
     */
    public static boolean canRelease(ResourcesModel allocatedResources, ResourcesModel usedResources,
                                     ResourcesModel releasedResources) {
        ResourcesModel releasableResources = subtractResources(allocatedResources, usedResources);
        if (releasableResources == null) {
            return false;
        }
        return !(releasableResources.getCpu() < releasedResources.getCpu()
                || releasableResources.getGpu() < releasedResources.getGpu()
                || releasableResources.getRam() < releasedResources.getRam());
    }

    private static boolean updateCpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                              ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getCpu() + facultyUsedResources.getCpu() <= facultyAllocatedResources.getCpu()) {
            facultyUsedResources.setCpu(usedResources.getCpu() + facultyUsedResources.getCpu());
        } else if (usedResources.getCpu() + facultyUsedResources.getCpu() - facultyAllocatedResources.getCpu()
                <= releasedResources.getCpu()) {
            releasedResources.setCpu(releasedResources.getCpu() - usedResources.getCpu() - facultyUsedResources.getCpu()
                    + facultyAllocatedResources.getCpu());
            facultyUsedResources.setCpu(facultyAllocatedResources.getCpu());
        } else {
            return false;
        }
        return true;
    }

    private static boolean updateGpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                              ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getGpu() + facultyUsedResources.getGpu() <= facultyAllocatedResources.getGpu()) {
            facultyUsedResources.setGpu(usedResources.getGpu() + facultyUsedResources.getGpu());
        } else if (usedResources.getGpu() + facultyUsedResources.getGpu() - facultyAllocatedResources.getGpu()
                <= releasedResources.getGpu()) {
            releasedResources.setGpu(releasedResources.getGpu() - usedResources.getGpu() - facultyUsedResources.getGpu()
                    + facultyAllocatedResources.getGpu());
            facultyUsedResources.setGpu(facultyAllocatedResources.getGpu());
        } else {
            return false;
        }
        return true;
    }

    private static boolean updateRam(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                              ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getRam() + facultyUsedResources.getRam() <= facultyAllocatedResources.getRam()) {
            facultyUsedResources.setRam(usedResources.getRam() + facultyUsedResources.getRam());
        } else if (usedResources.getRam() + facultyUsedResources.getRam() - facultyAllocatedResources.getRam()
                <= releasedResources.getRam()) {
            releasedResources.setRam(releasedResources.getRam() - usedResources.getRam() - facultyUsedResources.getRam()
                    + facultyAllocatedResources.getRam());
            facultyUsedResources.setRam(facultyAllocatedResources.getRam());
        } else {
            return false;
        }
        return true;
    }

    /** Calculates the final resources after usedResources are used if they can be and updates resources accordingly.
     *
     * @param usedResources resources to be used.
     * @param facultyUsedResources resources already used.
     * @param facultyAllocatedResources resources allocated for the faculty.
     * @param releasedResources resources in the released resource pool.
     * @return true if resources could be released, false otherwise.
     */
    public static boolean updateResources(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                                   ResourcesDatabaseModel facultyAllocatedResources,
                                   ResourcesDatabaseModel releasedResources) {
        return updateCpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateGpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateRam(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources);
    }
}
