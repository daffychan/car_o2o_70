package cn.wolfcode.car.common.utils;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;

import java.math.BigDecimal;
import java.util.Collection;

public class ActivitiesUtils {

    /**
     * 流程定义变量变量名需要定义
     * 解析流程定义文件，获取定义好价格边界值
     * 后面可扩展
     * @param bpmnModel
     * @param key 流程定义key
     * @return
     */
    public static BigDecimal getGTEDiscountPrice(BpmnModel bpmnModel,String key){
        BigDecimal discountPrice=new BigDecimal(0);
        Collection<FlowElement> flowElements = bpmnModel.getProcessById(key).getFlowElements();
        for (FlowElement element : flowElements) {
            if(element instanceof SequenceFlow){
                SequenceFlow flow=(SequenceFlow)element;
                String conditionExpression = flow.getConditionExpression();
                if(conditionExpression!=null && conditionExpression.startsWith("${disCountPrice>=")){
                    String disCountPrice = conditionExpression.split(">=")[1].replace("}","");
                    discountPrice=new BigDecimal(disCountPrice);
                }
            }
        }
        return discountPrice;
    }
}
