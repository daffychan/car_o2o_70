package cn.wolfcode.car.audit.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.car.appointment.domain.info.HistoryCommentInfo;
import cn.wolfcode.car.appointment.domain.vo.BusCarPackageAuditVo;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.wolfcode.car.common.annotation.Log;
import cn.wolfcode.car.common.core.controller.BaseController;
import cn.wolfcode.car.common.core.domain.AjaxResult;
import cn.wolfcode.car.common.enums.BusinessType;
import cn.wolfcode.car.audit.domain.BusCarPackageAudit;
import cn.wolfcode.car.audit.service.IBusCarPackageAuditService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;

/**
 * 套餐审核Controller
 * 
 * @author wolfcode
 * @date 2023-01-07
 */
@RestController
@RequestMapping("/audit/audit")
public class BusCarPackageAuditController extends BaseController
{
    @Autowired
    private IBusCarPackageAuditService busCarPackageAuditService;

    /**
     * 查询套餐审核列表
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusCarPackageAudit busCarPackageAudit)
    {
        startPage();
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectBusCarPackageAuditList(busCarPackageAudit);
        return getDataTable(list);
    }

    /**
     * 导出套餐审核列表
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:export')")
    @Log(title = "套餐审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusCarPackageAudit busCarPackageAudit)
    {
        List<BusCarPackageAudit> list = busCarPackageAuditService.selectBusCarPackageAuditList(busCarPackageAudit);
        ExcelUtil<BusCarPackageAudit> util = new ExcelUtil<BusCarPackageAudit>(BusCarPackageAudit.class);
        util.exportExcel(response, list, "套餐审核数据");
    }

    /**
     * 获取套餐审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busCarPackageAuditService.selectBusCarPackageAuditById(id));
    }

    /**
     * 新增套餐审核
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:add')")
    @Log(title = "套餐审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusCarPackageAudit busCarPackageAudit)
    {
        return toAjax(busCarPackageAuditService.insertBusCarPackageAudit(busCarPackageAudit));
    }

    /**
     * 修改套餐审核
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:edit')")
    @Log(title = "套餐审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusCarPackageAudit busCarPackageAudit)
    {
        return toAjax(busCarPackageAuditService.updateBusCarPackageAudit(busCarPackageAudit));
    }

    /**
     * 删除套餐审核
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:remove')")
    @Log(title = "套餐审核", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(busCarPackageAuditService.deleteBusCarPackageAuditByIds(ids));
    }


    /**
     * 我的待办
     * @return  返回代办的审核服务项信息
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:todoQuery')")
    @GetMapping("/todoQuery")
    public TableDataInfo todoQuery()
    {
        startPage();
        List<BusCarPackageAudit> list = busCarPackageAuditService.todoQuery();
        return getDataTable(list);
    }

    /**
     * 新增套餐审核
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:audit')")
    @Log(title = "套餐审核", businessType = BusinessType.INSERT)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody BusCarPackageAuditVo busCarPackageAuditVo)
    {
        busCarPackageAuditService.audit(busCarPackageAuditVo);
        return AjaxResult.success("审核成功");
    }



    /**审批历史
     * @param id 套餐信息 ID
     * @return  返回任务审批历史信息
     * HistoryCommentInfo ( 任务名称 开始时间 结束时间 持续时间 批注)
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:listHistory')")
    @GetMapping("/listHistory/{id}")
    public TableDataInfo listHistory(@PathVariable Long id)
    {
        startPage();
        List<HistoryCommentInfo> list = busCarPackageAuditService.listHistory(id);
        return getDataTable(list);
    }

    /**审批进度
     * @param id 审核套餐 ID
     * @return  返回 BpmnModel 流程图
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:process')")
    @GetMapping("/process/{id}")
    public void process(HttpServletResponse response,@PathVariable Long id) throws IOException {
        InputStream inputStream = busCarPackageAuditService.process(id);
        IOUtils.copy(inputStream,response.getOutputStream());
    }


    /**
     * 我的已办
     * 根据用户ID查询历史任务==>获取流程实例ID==>查询所有流程实例==>获取businessKey(套餐ID)
     *
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:done')")
    @GetMapping("/done")
    public TableDataInfo done(){
        startPage();
        List<BusCarPackageAudit> list = busCarPackageAuditService.doneQuery();
        return getDataTable(list);
    }

    /**
     * 套餐审核撤销
     * @param id 审核套餐ID
     * @return
     */
    @PreAuthorize("@ss.hasPermi('audit:audit:cancel')")
    @PutMapping("/cancel/{id}")
    public AjaxResult cancel(@PathVariable Long id) {
        busCarPackageAuditService.cancel(id);
        return AjaxResult.success("撤销成功");
    }
}
