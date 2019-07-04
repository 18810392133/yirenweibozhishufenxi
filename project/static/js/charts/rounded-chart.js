(function ($) {
 "use strict";
 
	 /*----------------------------------------*/
	/*  1.  pie Chart
	/*----------------------------------------*/
	var ctx = document.getElementById("piechart");
	var piechart = new Chart(ctx, {
		type: 'pie',
		data: {
			labels: ["iphone", "vivo", "android", "huawei", "oppo"],
			datasets: [{
				label: 'pie Chart',
                backgroundColor: [
					'rgba(255, 99, 132, 0.2)',
					'rgba(54, 162, 235, 0.2)',
					'rgba(255, 206, 86, 0.2)',
					'rgba(75, 192, 192, 0.2)',
					'rgba(153, 102, 255, 0.2)',
					'rgba(255, 159, 64, 0.2)'

				],
				borderColor: [
					'rgba(255,99,132,1)',
					'rgba(54, 162, 235, 1)',
					'rgba(255, 206, 86, 1)',
					'rgba(75, 192, 192, 1)',
					'rgba(153, 102, 255, 1)',
					'rgba(255, 159, 64, 1)'
				],
				data: [80, 60, 40, 30, 20]
            }]
		},
		options: {
			responsive: true
		}
	});
	 /*----------------------------------------*/
	/*  2.  polar Chart
	/*----------------------------------------*/
	var ctx = document.getElementById("polarchart");
	var polarchart = new Chart(ctx, {
		type: 'polarArea',
		data: {
			labels: ["Littlelittlelionbaby", "青青园中葵奎", "卡卡希希里", "IKUN二倩", "母狮玛利亚"],
			datasets: [{
				label: 'pie Chart',
				data: [189, 69, 60, 40, 32],
                 backgroundColor: [
					'rgba(255, 99, 132, 0.2)',
					'rgba(54, 162, 235, 0.2)',
					'rgba(255, 206, 86, 0.2)',
					'rgba(75, 192, 192, 0.2)',
					'rgba(153, 102, 255, 0.2)',
					'rgba(255, 159, 64, 0.2)'

				],
				borderColor: [
					'rgba(255,99,132,1)',
					'rgba(54, 162, 235, 1)',
					'rgba(255, 206, 86, 1)',
					'rgba(75, 192, 192, 1)',
					'rgba(153, 102, 255, 1)',
					'rgba(255, 159, 64, 1)'
				],
				
            }]
		},
		options: {
            responsive: true,
            legend: {
                 position: 'right',
            },
            title: {
                display: true,
                text: 'Polar Chart'
            },
            scale: {
              ticks: {
                beginAtZero: true
              },
              reverse: false
            },
            animation: {
                animateRotate: false,
                animateScale: true
            }
        }
	});
	
	 /*----------------------------------------*/
	/*  3.  radar Chart
	/*----------------------------------------*/
	var ctx = document.getElementById("radarchart");
	var radarchart = new Chart(ctx, {
		type: 'radar',
		data: {
			labels: ["Design", "Development", "Graphic", "Android", "Games"],
			datasets: [{
				label: "My First dataset",
				data: [90, 20, 30, 40, 10],
                backgroundColor: '#006DF0',
                borderColor: '#006DF0',
                pointBackgroundColor: '#ff0000',
				
            },{
				label: "My Second dataset",
				data: [50, 20, 10, 30, 90],
                backgroundColor: '#933EC5',
                borderColor: '#933EC5',
                pointBackgroundColor: '#ff0000',
				
            }]
		},
		options: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Radar Chart'
            },
            scale: {
              ticks: {
                beginAtZero: true
              }
            }
        }
	});
	 /*----------------------------------------*/
	/*  3.  Doughnut Chart
	/*----------------------------------------*/
	var ctx = document.getElementById("Doughnutchart");
	var Doughnutchart = new Chart(ctx, {
		type: 'radar',
		data: {
			labels: ["Red", "Orange", "Yellow", "Green", "Blue"],
			datasets: [{
				label: 'Dataset 1',
				data: [10, 20, 30, 40, 90],
                backgroundColor: '#006DF0'
				
            }]
		},
		options: {
            responsive: true,
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Doughnut Chart'
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
	});
	
	

	 
		
})(jQuery); 