module.exports = {

    generateHTML : function(user, title, body, heading){

    	return `<!DOCTYPE html>
    	<html lang="en">

    	<head>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>`+title+`</title>

        <!-- Bootstrap Core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">

        <!-- Template CSS -->
        <link href="css/sb-admin.css" rel="stylesheet">

        <!-- Custom Fonts -->
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

        <!-- MylocalBartender CSS -->
        <link href="css/mylocalbartender.css" rel="stylesheet">

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

        <!-- MyLocalBartender Core JavaScript -->
        <script src="js/mylocalbartender.js"></script>

    </head>

    <body>

        <div id="wrapper">

            <!-- Navigation -->
            <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand page-link" href="./">My Local Bartender</a>
                </div>
                <!-- Top Menu Items -->
                <ul class="nav navbar-right top-nav">         
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" id="login_user" class="page-link"><i class="fa fa-user"></i> `+user+` <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li>
                                <a href="./profile" class="page-link"><i class="fa fa-fw fa-user"></i> Profile</a>
                            </li>
                            <li class="divider"></li>
                            <li>
                                <a href="./logout"><i class="fa fa-fw fa-power-off"></i> Log Out</a>
                            </li>
                        </ul>
                    </li>
                </ul>
                <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
                <div class="collapse navbar-collapse navbar-ex1-collapse">
                    <ul class="nav navbar-nav side-nav">
                        <li>
                            <a href="./" class="page-link">Dashboard</a>
                        </li>
                        <li>
                            <a href="#" data-toggle="collapse" data-target="#">Tables <i class="fa fa-fw fa-caret-down"></i></a>
                            <ul id="table-drop-down" class="panel-collapse collapse in">
                                <li>
                                    <a href="./tables%20online" class="page-link">Online Users</a>
                                </li>
                                <li>
                                    <a href="./tables%20job" class="page-link">Job Table</a>
                                </li>
                                <li>
                                    <a href="./tables%20user" class="page-link">User Table</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#" data-toggle="collapse" data-target="#">Admin Managment Tools <i class="fa fa-fw fa-caret-down"></i></a>
                            <ul id="tools-drop-down" class="panel-collapse collapse in">
                                <li>
                                    <a href="./add%20admin" class="page-link">Add Admin</a>
                                </li>
                                <li>
                                    <a href="./delete%20job" class="page-link">Delete Job</a>
                                </li>
                                <li>
                                    <a href="./delete%20account" class="page-link">Delete Account</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <!-- /.navbar-collapse -->
            </nav>
            
            <div id="page-wrapper">

                <div class="container-fluid">

                    <!-- Page Heading -->
                    <div class="row">
                        <div class="col-lg-12">
                            <h1 class="page-header">`+heading+`</h1>
                        </div>
                    </div>`
                    +body+
                `</div>

            </div>
            <!-- /#page-wrapper -->

        </div>
        <!-- /#wrapper -->

        <!-- jQuery -->
        <script src="js/jquery.js"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="js/bootstrap.min.js"></script>

    </body>

    </html>`;
    },

    deleteEntryFragment: function(field_name, field_id){

        return `<div id="error_message_placeholder" class="hidden">
            <div class="row">
                <div id="`+field_id+`_error" class="alert alert-danger col-lg-12"></div>
                <div id="reason_error" class="alert alert-danger col-lg-12"></div>
            </div>
        </div>
        <!-- /.row -->
        <div class="form-group">
            <div id="`+field_id+`_div" class="row">
                <div class="col-lg-2">
                    <label>`+field_name+`</label>
                    <div class="form-group input-group">
                        <span class="input-group-addon">ID</span>
                        <input id="`+field_id+`" type="number" class="form-control" min=0>
                    </div>
                </div>
            </div>
            <div id="reason_div" class="row">
                <div class="col-lg-6">
                    <label>Reason</label>
                    <textarea id="reason" row="3" class="form-control"></textarea>
                </div>
            </div>
        </div>
        <button onclick="deleteEntryFromTable('`+field_id+`', 'reason')" class="btn btn-primary btn-lg">Delete</button>`;
    },

    generateTable: function(tableName, fields, rows){

        this.createHeader = function(){

            let heads = '';

            for(i = 0 ; i < fields.length ; ++i){

                heads += '<th>' + fields[i] + '</th>';

                if(i < fields.length - 1) heads += '\n';
            }

            return `<thead>
                        <tr>
                           `+heads+`
                        </tr>
                    </thead>`;
            }

        this.addData = function(){

            let heads = '';
            console.log(fields);

            for(i = 0 ; i <  rows.length ; ++i){

                let data = '';

                for(j = 0 ; j < fields.length ; ++j){

                    data += '<td>' + rows[i][ (fields[j]) ] + '</td>';

                    data += '\n';

                }

                heads += '<tr>\n'+data+'\n</tr>';
            }
            
            return `<tbody>
                        `+heads+`
                    </tbody>`
        }

        /*this.createFilters = function(){

            let heads = '';

            for(i = 0 ; i < fields.length ; ++i){

                heads += `<label>filter `+fields[i]+`:</label>
                            <input id="`+fields[i]+`_filter" class="form-control">
                            <br>`

                if(i < fields.length - 1) heads += '\n';
            }

            return `<div class="col-lg-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Filter with SQL</h3>
                            </div>
                            <div class="panel-body">
                                <form role="form">
                                    <div class="form-group">
                                        <label>Set filter (SQL WHERE clause):</label>
                                        <input id="`+tableName+`_general_filter" class="form-control">
                                        <br>
                                        <button type="button" onclick="generalFilter()" class="btn btn-default">filter</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">General filter</h3>
                            </div>
                            <div class="panel-body">
                                <form role="form">
                                    <div class="form-group">
                                        `+heads+`
                                        <button type="button" onclick="seperatedFilter()" class="btn btn-default">filter</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>`;

        }*/

        this.createBody = function(){

            return `<div class="row">    
                    <div class="col-lg-12">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                `+this.createHeader()+`
                                <tbody>
                                    `+this.addData()+`
                                </tbody>
                            </table>
                        </div>
                    </div>
                    `+/*this.createFilters()+*/`
                    </div>
                </div>`;
        }

        return this.createBody();
    }
};