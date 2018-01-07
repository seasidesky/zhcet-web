package in.ac.amu.zhcet.service.firebase.messaging.data;

import in.ac.amu.zhcet.service.firebase.messaging.data.request.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRequest {
    private Message message;
}
