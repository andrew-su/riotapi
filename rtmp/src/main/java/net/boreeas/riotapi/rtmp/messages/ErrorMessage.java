/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rtmp.messages;

import lombok.*;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * Created on 6/11/2014.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Serialization(name = "flex.messaging.messages.ErrorMessage")
public class ErrorMessage extends AcknowledgeMessage {
    private String faultCode;
    private String faultString;
    private String faultDetail;
    private Object rootCause;
    private Object extendedData;
}
