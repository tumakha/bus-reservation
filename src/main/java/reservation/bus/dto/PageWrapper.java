package reservation.bus.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Value
public class PageWrapper<T> {

    private Page<T> page;

    public List<T> getContent() {
        return page.getContent();
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }

    public long getTotalElements() {
        return page.getTotalElements();
    }

    public int getPage() {
        return page.getNumber();
    }

    public int getSize() {
        return page.getSize();
    }

}
