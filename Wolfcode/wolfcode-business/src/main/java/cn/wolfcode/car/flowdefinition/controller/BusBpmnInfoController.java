package cn.wolfcode.car.flowdefinition.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOCase;
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
import cn.wolfcode.car.flowdefinition.domain.BusBpmnInfo;
import cn.wolfcode.car.flowdefinition.service.IBusBpmnInfoService;
import cn.wolfcode.car.common.utils.poi.ExcelUtil;
import cn.wolfcode.car.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义Controller
 * 
 * @author wolfcode
 * @date 2023-01-06
 */
@RestController
@RequestMapping("/flowdefinition/info")
public class BusBpmnInfoController extends BaseController
{
    @Autowired
    private IBusBpmnInfoService busBpmnInfoService;

    /**
     * 查询流程定义列表
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusBpmnInfo busBpmnInfo)
    {
        startPage();
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        return getDataTable(list);
    }

    /**
     * 导出流程定义列表
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:export')")
    @Log(title = "流程定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusBpmnInfo busBpmnInfo)
    {
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        ExcelUtil<BusBpmnInfo> util = new ExcelUtil<BusBpmnInfo>(BusBpmnInfo.class);
        util.exportExcel(response, list, "流程定义数据");
    }

    /**
     * 获取流程定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busBpmnInfoService.selectBusBpmnInfoById(id));
    }

    /**
     * 新增流程定义
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:add')")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusBpmnInfo busBpmnInfo)
    {
        return toAjax(busBpmnInfoService.insertBusBpmnInfo(busBpmnInfo));
    }

    /**
     * 修改流程定义
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusBpmnInfo busBpmnInfo)
    {
        return toAjax(busBpmnInfoService.updateBusBpmnInfo(busBpmnInfo));
    }

    /**
     * 删除流程定义
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:remove')")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(busBpmnInfoService.deleteBusBpmnInfoById(id));
    }

    /**
     * 流程定义部署
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:deploy')")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PostMapping("/deploy")
    public AjaxResult deploy(MultipartFile file,String bpmnLabel,Integer bpmnType,String info) throws IOException {
        //接收 参数(部署文件,服务类型,服务名称,备注)
        busBpmnInfoService.deploy(file,bpmnLabel,bpmnType,info);
        return AjaxResult.success("部署成果");
    }

    /**
     * 获取流程定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('flowdefinition:info:readResource')")
    @GetMapping(value = "/read/{type}/{id}")
    public void read(HttpServletResponse response,@PathVariable String type,@PathVariable Long id) throws IOException {
        InputStream inputStream = busBpmnInfoService.readResource(type,id);
        IOUtils.copy(inputStream,response.getOutputStream());

    }
}
