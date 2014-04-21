#!/usr/bin/php -qd open_basedir=/
<?php
///
// 作业名：事件驱动分发作业
// 功能：件comm_event_queue 的数据到product_review_event_handle、product_question_event_handle、product_bookshelf_event_handle表
//       和com_review_count_event_handle表。数据供同步到单品的作业和百货评论计分作业使用
// 运行周期：一直运行
// crontab：无
// 日志文件：/var/www/cron/shcron/log/sh_distribution_event.log
// twiki位置：
// 负责人：于学忠2010-03-15
// 作业编号：
///
if (!defined("ROOT"))
    define("ROOT", dirname(__FILE__) . "/inc");

include_once(ROOT . "/conf.inc");
include_once(ROOT . "/data/myAccessPDO.inc");
include_once(ROOT . "/data/db.inc");
include_once(ROOT . "/data/eventDataHandler.inc");
include_once(ROOT . "/data/reviewDataHandler.inc");
include_once(ROOT . "/data/questionDataHandler.inc");
include_once(ROOT . "/data/bookshelfDataHandler.inc");
include_once(ROOT . "/data/summaryCountDataHandler.inc");
include_once(ROOT . "/data/comSummaryCountDataHandler.inc");
include_once(ROOT . "/data/toolBarDataHandler.inc");
//include_once(ROOT . "/data/commEventMonitorDataHandler.inc");
include_once(ROOT . "/data/commProductReviewDataHandler.inc");
include_once(ROOT . "/data/commproduct/commProductQuestionDataHandler.inc");

$event_id = getMaxEvent();
//print_r($event_id);
$event_set = eventDataHandler::getEvent($event_id);
//print_r($event_set);
$insert_subject_id = array();

$review_handler = new ReviewDataHandler();
$bookshelf_handler = new bookshelfDataHandler();

if (!$event_set)
    echo "\nevent num=0";

if (!empty($event_set)) {
    foreach ($event_set as $event) {
        $event_type_id = intval($event["event_type_id"]);

        //推数据到单品
        if (in_array($event_type_id, array(210300011, 210300012, 200200001, 210300001, 210300007, 210300003, 210300004, 200200010, 210300005, 210300010, 200200004, 200200005, 210300006))) {
            echo "\n推社区数据到单品事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"] . "\n";
            $productId = getProductIdBySourceId(intval($event["source_type_id"]), intval($event["source_id"]));
            if ($productId)
                summaryCountDataHandler::replace_sync_to_product($productId);
        }

        //黑名单处理
        if (in_array($event_type_id, array(210300011, 210300012, 210300013, 210300014, 210300015, 210300016, 210300001, 210300005, 210300019, 210300002, 210300006, 210300022))) {
            echo "\n加入黑白名单事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_customer_black_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
        }
        //放入动态事件表
        if (in_array($event_type_id, array(200200001, 210300001, 200200009, 200200010, 200200011, 200200017, 200200019, 200200021))) {
            echo "\n加入动态事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_dynamic_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
        }
        //放入用户社区信息统计事件表
        if (in_array($event_type_id, array(200200001, 210300001, 210300007, 210300003, 210300004, 210300011, 200200007, 200200008, 200200011, 200200014, 200200031))) {
            echo "\n加入用户社区信息统计事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            //eventDataHandler::insert_customer_summary_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
            $userId = getUserIdBySouceid(intval($event["source_type_id"]), intval($event["source_id"]));
            if ($userId)
                comSummaryCountDataHandler::replace_sync_to_customer($userId);
        }
        //放入单品社区信息统计事件表
        if (in_array($event_type_id, array(200200001, 210300001, 210300007, 210300005, 210300010, 200200004, 210300011, 210300012, 200200031))) {
            echo "\n加入单品社区信息统计事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            //eventDataHandler::insert_subject_summary_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
            $productId = getProductIdBySourceId(intval($event["source_type_id"]), intval($event["source_id"]));
            if ($productId)
                comSummaryCountDataHandler::replace_sync_to_subject($productId);
        }
        //放入积分处理事件表
//        if (in_array($event_type_id, array( 210300011,300000005,300000006 ,300000007 ))) {
//            echo "\n加入单品社区信息统计事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
//            eventDataHandler::insert_point_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
//        }
        //放入动态监控处理事件表 顾客发商品评论
        if (in_array($event_type_id, array(200200001))) {
            echo "\n加入动态监控处理事件表event_id" . $event["event_id"] . "\n";
            //   commEventMonitorDataHandler::insert_event_hander($event["event_type_id"]);
        }

        //用户tool_bar相关事件处理
        if (in_array($event_type_id, array(200000041, 200000042, 200200001))) {
            echo "\n 加入用户tool_bar相关处理事件表event_id" . $event["event_id"] . " \n";
            toolBarDataHandler::insert_event_handler($event["event_id"], $event["source_id"], $event["event_type_id"], 0);
        }


        //新评论推送到单品事件处理
        if (in_array($event_type_id, array(200200001, 210300003, 210300004, 210300001, 210300007, 210300011, 200200002, 210300002, 210300008, 200200010, 210300014, 210300025, 201300029, 200200031))) {
            echo "\n加入新评论推送到单品事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            commProductReviewDataHandler::insert_product_review_event_handler($event["event_id"], $event["source_id"], $event["source_type_id"], $event["event_type_id"]);
        }

        // 问答数据推送到单品事件处理
        if (in_array($event_type_id, array(210300005, 210300010, 200200004, 200200005, 210300006, 210300009, 210300012, 210300015))) {
            echo "\n加入问答数据推送到单品事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            commProductQuestionDataHandler::insert_product_question_sync_event_handler($event["event_id"], $event["source_id"], $event["source_type_id"], $event["event_type_id"]);
        }

        // 放入Toolbar评论求关注事件表
        if (in_array($event_type_id, array(200200001))) {
            echo "\n加入评论求关注事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            $args = array('review_id' => $event["source_id"]);
            eventDataHandler::insert_toolbar_review_attention_event_handler($event["event_id"], $event["event_type_id"], json_encode($args));
        }

        // 更新商品评论状态事件
        if (in_array($event_type_id, array(200200001))) {
            echo "\n加入更新商品评论状态事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_order_review_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
        }

        // 评论回复事件
        if (in_array($event_type_id, array(200200002))) {
            echo "\n加入评论回复事件状态事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_review_reply_event_handle($event["event_id"], $event["source_id"], $event["event_type_id"]);
        }

        // 写评论property事件
        if (in_array($event_type_id, array(200200001))) {
            echo "\n加入写评论property事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_review_property_event_handler($event["event_id"], $event["source_id"], $event["source_type_id"], $event["event_type_id"]);
        }

        // 单品统计事件
        if (in_array($event_type_id, array(210400001))) {
            echo "\n加入单品统计事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_product_summary_event_handler($event["event_id"], $event["source_id"], $event["source_type_id"], $event["event_type_id"]);
        }

        //score push 事件
        if (in_array($event_type_id, array(210300011,200200001))) {
            echo "\n加入score push事件 event_id:" . $event["event_id"] . "source_id:" . $event["source_id"] . "event_type_id:" . $event["event_type_id"];
            eventDataHandler::insert_push_score_event_handler($event["event_id"], $event["source_id"], $event["source_type_id"], $event["event_type_id"]);
        }

        $event_id = intval($event["event_id"]) > $event_id ? intval($event["event_id"]) : $event_id;
        echo "\nmax event id" . $event_id . "\n";
    }
    print_r($event_id);
    setMaxEvent($event_id);
}

function setMaxEvent($id) {

    $result = eventDataHandler::select_EventLog_lastId(0);
    if ($result)
        eventDataHandler::update_EventLog($id, 0);
    else
        eventDataHandler::insert_EventLog($id, 0);
}

function getMaxEvent() {
    $id = 0;
    $result = eventDataHandler::select_EventLog_lastId(0);
    $result && $id = $result["last_id"];
    return $id;
}

function getProductIdBySourceId($souce_type_id, $source_id) {
    $product_id = "";
    switch ($souce_type_id) {
        case 1001:
            /*
              $review_handler = new ReviewDataHandler();
              $row = $review_handler->get_review_id_byid($source_id);
              if (!empty($row)) {
              $product_id = $row["subject_id"];
              }
             */
            $row = comSummaryCountDataHandler::get_review_id_byid($source_id);
            if (!$row->isEmpty()) {
                $product_id = $row->row("subject_id");
                echo "pid:$product_id\n";
            }
            break;
        case 1003:
            $row = questionDataHandler::get_question_byid($source_id);
            if (!empty($row)) {
                $product_id = $row["subject_id"];
            }
            break;
        case 1006:
            $bookshelf_handler = new bookshelfDataHandler();
            $row = $bookshelf_handler->select_by_id($source_id);
            if (!empty($row)) {
                $product_id = $row["product_id"];
            }
            break;
        case 2004://商品自动打分事件
            $auto_score = comSummaryCountDataHandler::get_auto_score_by_id($source_id);
            if (!empty($auto_score)) {
                $product_id = $auto_score->row("subject_id");
            }
            break;
    }
    return $product_id;
}

function getUserIdBySouceid($souce_type_id, $source_id) {
    $user_id = "";
    switch ($souce_type_id) {
        case 1001:
            /*
              $review_handler = new ReviewDataHandler();
              $row = $review_handler->get_review_id_byid($source_id);
              if (!empty($row)) {
              $user_id = $row["customer_id"];
              }
             */
            $row = comSummaryCountDataHandler::get_review_id_byid($source_id);
            if (!$row->isEmpty()) {
                $user_id = $row->row("customer_id");
                echo "cust_id:$user_id\n";
            }
            break;
        case 1005:
            $intrested = comSummaryCountDataHandler::get_intrested_byid($source_id);
            if (!empty($intrested)) {
                $user_id = $intrested["customer_id"];
            }
            break;
        case 1006:
            $bookshelf = comSummaryCountDataHandler::get_bookshelf_by_id($source_id);
            if (!empty($bookshelf)) {
                $user_id = $bookshelf["customer_id"];
            }
            break;
        case 2004://商品自动打分事件
            $auto_score = comSummaryCountDataHandler::get_auto_score_by_id($source_id);
            if (!empty($auto_score)) {
                $user_id = $auto_score->row("customer_id");
            }
            break;
    }
    return $user_id;
}
?>
