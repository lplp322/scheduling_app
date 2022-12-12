package nl.tudelft.sem.waitinglist.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import org.junit.jupiter.api.Test;

class ResourcesTest {
    @Test
    void zeroCpu() {
        assertThatThrownBy(() -> new Resources(0, 2, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeCpu() {
        assertThatThrownBy(() -> new Resources(-1, 2, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeGpu() {
        assertThatThrownBy(() -> new Resources(1, -1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroRam() {
        assertThatThrownBy(() -> new Resources(5, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeRam() {
        assertThatThrownBy(() -> new Resources(5, 4, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void gpuGreaterThanCpu() {
        assertThatThrownBy(() -> new Resources(5, 6, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ramGreaterThanCpu() {
        assertThatThrownBy(() -> new Resources(5, 5, 6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void successfulConstruction() {
        Resources resources = new Resources(5, 2, 2);
        assertThat(resources.getCpu()).isEqualTo(5);
        assertThat(resources.getGpu()).isEqualTo(2);
        assertThat(resources.getRam()).isEqualTo(2);
    }

    @Test
    void nullResourceModel() {
        assertThatThrownBy(() -> new Resources(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void successfulConstructionFromResourceModel() {
        ResourcesModel resourcesModel = new ResourcesModel(6, 5, 1);
        Resources resources = new Resources(resourcesModel);
        assertThat(resources.getCpu()).isEqualTo(6);
        assertThat(resources.getGpu()).isEqualTo(5);
        assertThat(resources.getRam()).isEqualTo(1);
    }
}