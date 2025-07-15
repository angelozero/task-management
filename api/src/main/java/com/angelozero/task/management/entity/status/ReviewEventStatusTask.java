package com.angelozero.task.management.entity.status;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewEventStatusTask extends CustomEventStatusTask {

    private String reviewerName;

}
