package com.alibaba.smart.framework.engine.bpmn.behavior.gateway;

import com.alibaba.smart.framework.engine.behavior.base.AbstractActivityBehavior;
import com.alibaba.smart.framework.engine.bpmn.assembly.gateway.ExclusiveGateway;
import com.alibaba.smart.framework.engine.context.ExecutionContext;
import com.alibaba.smart.framework.engine.extension.annoation.ExtensionBinding;
import com.alibaba.smart.framework.engine.extension.constant.ExtensionConstant;
import com.alibaba.smart.framework.engine.pvm.PvmActivity;
import com.alibaba.smart.framework.engine.pvm.PvmTransition;
import com.alibaba.smart.framework.engine.pvm.event.EventConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@ExtensionBinding(group = ExtensionConstant.ACTIVITY_BEHAVIOR, bindKey = ExclusiveGateway.class)
public class ExclusiveGatewayBehavior extends AbstractActivityBehavior<ExclusiveGateway> {

    public ExclusiveGatewayBehavior() {
        super();
    }

    @Override
    public boolean enter(ExecutionContext context, PvmActivity pvmActivity) {
        super.enter(context, pvmActivity);

        // 用上下文暂停信息覆盖，排他网关支持暂停
        return context.isNeedPause();
    }

    @Override
    public void leave(ExecutionContext context, PvmActivity pvmActivity) {

        fireEvent(context, pvmActivity, EventConstant.ACTIVITY_END);


        //执行每个节点的hook方法
        Map<String, PvmTransition> outcomeTransitions = pvmActivity.getOutcomeTransitions();
        if (outcomeTransitions == null || outcomeTransitions.isEmpty()) {
            log.warn("ExclusiveGateway No outcome transitions activityInstanceId={}", context.getActivityInstance().getInstanceId());
            return;
        }

        ExclusiveGatewayBehaviorHelper.chooseOnlyOne(pvmActivity, context, outcomeTransitions);
    }
}
