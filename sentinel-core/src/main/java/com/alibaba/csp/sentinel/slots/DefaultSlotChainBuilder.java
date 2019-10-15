/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots;

import com.alibaba.csp.sentinel.slotchain.DefaultProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.SlotChainBuilder;
import com.alibaba.csp.sentinel.slots.block.authority.AuthoritySlot;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeSlot;
import com.alibaba.csp.sentinel.slots.block.flow.FlowSlot;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.csp.sentinel.slots.logger.LogSlot;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlot;
import com.alibaba.csp.sentinel.slots.system.SystemSlot;

/**
 * Builder for a default {@link ProcessorSlotChain}.
 *
 * @author qinan.qn
 * @author leyou
 */
public class DefaultSlotChainBuilder implements SlotChainBuilder {

    @Override
    public ProcessorSlotChain build() {
        ProcessorSlotChain chain = new DefaultProcessorSlotChain();
        chain.addLast(new NodeSelectorSlot());  // 收集资源的路径，并将这些资源的调用路径，以树状结构存储起来
        chain.addLast(new ClusterBuilderSlot());    // 用于存储资源的统计信息以及调用者信息，例如该资源的 RT, QPS, thread count 等等
        chain.addLast(new LogSlot());   // 用于记录blockException信息的日志信息，会写入的日志文件中；
        chain.addLast(new StatisticSlot()); // 用于记录、统计不同纬度的 runtime 指标监控信息；
        chain.addLast(new AuthoritySlot()); // 根据配置的黑白名单和调用来源信息，来做黑白名单控制；
        chain.addLast(new SystemSlot());    // 通过系统的状态，例如 load1 等，来控制总的入口流量；
        chain.addLast(new FlowSlot());  // 用于根据预设的限流规则以及前面 slot 统计的状态，来进行流量控制；
        chain.addLast(new DegradeSlot());   // 通过统计信息以及预设的规则，来做熔断降级；

        return chain;
    }

}
