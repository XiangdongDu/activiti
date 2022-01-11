$(document).ready(function () {
    $("#dept").hide();
    var grid = $("#grid-data").bootgrid({
        navigation: 2,
        columnSelection: false,
        ajax: true,
        url: "depttasklist",
        formatters: {
            "taskcreatetime": function (column, row) {
                return getLocalTime(row.taskcreatetime);
            },
            "commands": function (column, row) {
                return "<button class=\"btn btn-xs btn-default ajax-link command-run1\" data-row-id=\"" + row.taskid + "\">处理</button>";
            }
        }

    }).on("loaded.rs.jquery.bootgrid", function () {
        grid.find(".command-run1").on("click", function (e) {
            var taskid = $(this).data("row-id");
            $.post("dealtask", {"taskid": taskid}, function (data) {
                var obj = data;
                //dxd 判断是否需要HR隐藏
                if (obj.days == 1) {//隐藏
                    document.getElementById('isDisplay').style.display = "none";
                } else {//展示
                    document.getElementById('isDisplay').style.display = "block";
                }
                $("#reason").val(obj.reason);
                $("#type").val(obj.leave_type);
                $("#userid").val(obj.user_id);
                $("#startime").val(obj.start_time);
                $("#endtime").val(obj.end_time);
                $("#applytime").val(obj.apply_time);
                $("form").attr("action", "task/deptcomplete/" + taskid);
                $("form").data("taskid", taskid);
            });
            $("#dept").show();

        });


    });
});

function getLocalTime(nS) {
    return new Date(parseInt(nS)).toLocaleString().replace(/:\d{1,2}$/, ' ');
}


