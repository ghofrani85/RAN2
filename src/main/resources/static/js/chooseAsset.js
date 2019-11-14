
        function chooseAssetModal()
        {
            //"listener" for select table row if it is clicked
            $("#chooseAssetTable > tbody > tr").click(onEventSelectRow);
        }


        function onEventSelectRow()
        {
            // row was clicked

            //remove old tableRowSelected mark from a (probably) other row
            $("tr.tableRowSelected").removeClass("tableRowSelected");

            //select new
            $( this ).addClass("tableRowSelected");

            //put selected asset id into form
            var row = document.getElementsByClassName("tableRowSelected")[0];
            var id = row.children[0].getAttribute("data-id");
            var idElement = document.getElementById("chooseAssetFormAssetId");
            idElement.value = id;

            //enable submit button
            $('#chooseAssetFormSubmit').prop('disabled', false);

        }

        $( document ).ready( chooseAssetModal );